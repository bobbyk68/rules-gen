package uk.gov.hmrc.rules.ir;

import uk.gov.hmrc.rules.RuleRow;
import uk.gov.hmrc.rules.parsing.ConditionRole;
import uk.gov.hmrc.rules.parsing.ParsedCondition;

import java.util.List;

public class RuleIrGenerator {

    private final TopologyBuilder topologyBuilder = new TopologyBuilder();

    public RuleModel generate(RuleRow row, List<ParsedCondition> parsedConditions) {

        List<TopologyBuilder.ParentGroup> groups =
            topologyBuilder.build(parsedConditions);

        RuleModel model = new RuleModel(row, parsedConditions);
        model.setId(row.id());

        int parentIndex = 1;
        int childIndex  = 1;

        for (TopologyBuilder.ParentGroup group : groups) {
            String parentAlias = "$gi" + parentIndex++;

            ParentConditionNode parentNode = new ParentConditionNode();
            parentNode.setAlias(parentAlias);
            parentNode.setFactType(group.getParentEntityType());
            parentNode.setRole(ConditionRole.OTHER);
            parentNode.setFieldTypeLabel(null);

            addHeaderConstraints(parentNode, row);
            model.getConditions().add(parentNode);

            for (ParsedCondition pc : group.getChildren()) {
                FactConditionNode child = new FactConditionNode();
                child.setAlias("$c" + childIndex++);
                child.setFactType(pc.getEntityType());
                child.setParentAlias(parentAlias);
                child.setRole(pc.getRole());
                child.setFieldTypeLabel(pc.getFieldTypeLabel());

                ParsedCondition.Quantifier q = pc.getQuantifier();
                boolean isAll = q == ParsedCondition.Quantifier.ALL;

                String op = pc.getOperator() == null
                        ? ""
                        : pc.getOperator().trim().toUpperCase();

                // IF (PRIMARY) must never be flipped. THEN (SECONDARY) becomes a violation trigger.
                boolean isThen = pc.getRole() == ConditionRole.SECONDARY;

                if (!isThen) {
                    // IF side: scenario gate (keep it literal)
                    child.setExistence(FactConditionNode.Existence.EXISTS);
                    // op unchanged
                } else {
                    // THEN side: requirement -> violation trigger
                    if (isAll) {
                        // ALL must satisfy X -> violation when EXISTS(not X)
                        child.setExistence(FactConditionNode.Existence.EXISTS);
                        op = negateOperator(op);   // IN->NOT_IN, ==->!=, etc.
                    } else {
                        // AT_LEAST_ONE must satisfy X
                        // Default violation: NOT_EXISTS(X)
                        //
                        // BUT bible wants unary "provided" inverted as EXISTS(not provided)
                        if ("IS_PROVIDED".equals(op)) {
                            child.setExistence(FactConditionNode.Existence.EXISTS);
                            op = "IS_NOT_PROVIDED";
                        } else if ("IS_NOT_PROVIDED".equals(op)) {
                            child.setExistence(FactConditionNode.Existence.EXISTS);
                            op = "IS_PROVIDED";
                        } else {
                            child.setExistence(FactConditionNode.Existence.NOT_EXISTS);
                            // op unchanged
                        }
                    }
                }

                System.out.println("DEBUG IR: role=" + pc.getRole()
                        + " quant=" + pc.getQuantifier()
                        + " existence=" + child.getExistence()
                        + " field=" + pc.getFieldName()
                        + " op=" + op
                        + " values=" + pc.getValues());


                System.out.println("DEBUG isUnary? op=" + op + " -> " + isUnary(op)
                        + " values.size=" + pc.getValues().size());

                // Unary operators: no RHS values, but still a real constraint
                if (isUnary(op)) {
                    child.getFieldConstraints().put(
                            pc.getFieldName(),
                            new Constraint(op, Boolean.TRUE)
                    );

                // Pure existence: presence of the child fact is enough
                } else if ("EXISTS".equals(op)) {
                    // no field constraint

                } else if (pc.getValues().isEmpty()) {
                    throw new IllegalStateException("Operator " + op + " requires values for " + pc);

                } else {
                    // If IN/NOT_IN has a single value, collapse to ==/!= for cleaner DSL keys/wording
                    if ("IN".equals(op) || "NOT_IN".equals(op)) {
                        child.getFieldConstraints().put(
                                pc.getFieldName(),
                                new Constraint(op, pc.getValues()) // keep as List always
                        );
                    } else {
                        child.getFieldConstraints().put(
                                pc.getFieldName(),
                                new Constraint(op, pc.getValues().get(0))
                        );
                    }


                    if ("IN".equals(op) || "NOT_IN".equals(op)) {
                        child.getFieldConstraints().put(
                                pc.getFieldName(),
                                new Constraint(op, pc.getValues())
                        );
                    } else {
                        child.getFieldConstraints().put(
                                pc.getFieldName(),
                                new Constraint(op, pc.getValues().get(0))
                        );
                    }
                }


                model.getConditions().add(child);
            }

        }

        EmitErrorActionNode emit = new EmitErrorActionNode();
        emit.setBrCode(deriveBrCode(row));
        emit.setDmsErrorCode(row.errorCode());
        model.getActions().add(emit);

        return model;
    }


    private static String negateOperator(String op) {
        if (op == null) return "";

        return switch (op.trim().toUpperCase()) {
            case "IN"     -> "NOT_IN";
            case "NOT_IN" -> "IN";
            case "=="     -> "!=";
            case "="      -> "!=";
            case "!="     -> "==";

            case "<"      -> ">=";
            case "<="     -> ">";
            case ">"      -> "<=";
            case ">="     -> "<";

            default -> throw new IllegalArgumentException("No negation for operator: " + op);
        };
    }


    private void addHeaderConstraints(ParentConditionNode parent, RuleRow row) {

        List<String> decTypes = row.declarationType();
        if (decTypes != null && !decTypes.isEmpty()) {
            if (decTypes.size() == 1) {
                parent.getFieldConstraints().put(
                    "declarationType",
                    new Constraint("==", decTypes.get(0))
                );
            } else {
                parent.getFieldConstraints().put(
                    "declarationType",
                    new Constraint("IN", decTypes)
                );
            }
        }

        List<String> procCats = row.procedureCategory();
        if (procCats != null && !procCats.isEmpty()) {
            if (procCats.size() == 1) {
                parent.getFieldConstraints().put(
                    "procedureCategory",
                    new Constraint("==", procCats.get(0))
                );
            } else {
                parent.getFieldConstraints().put(
                    "procedureCategory",
                    new Constraint("IN", procCats)
                );
            }
        }
    }

    private String deriveBrCode(RuleRow row) {
        String param = row.param();
        if (param != null && !param.isBlank()) {
            return param.trim();
        }
        String id = row.id();
        if (id == null) {
            return "";
        }
        int idx = id.indexOf('_');
        return (idx > 0) ? id.substring(0, idx) : id;
    }

    private static boolean isUnary(String op) {
        if (op == null) return false;
        return switch (op.trim().toUpperCase()) {
            case "IS_PROVIDED", "IS_NOT_PROVIDED",
                 "IS_PRESENT", "IS_NOT_PRESENT" -> true;

            default -> false;
        };
    }


}
