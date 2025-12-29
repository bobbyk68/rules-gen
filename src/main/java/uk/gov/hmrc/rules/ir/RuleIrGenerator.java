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

        RuleModel model = new RuleModel();
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

                if (pc.getValues().isEmpty()) {
                    // EXISTS-only: presence of fact is enough
                } else if ("IN".equalsIgnoreCase(pc.getOperator())
                        || "NOT_IN".equalsIgnoreCase(pc.getOperator())) {
                    child.getFieldConstraints().put(
                        pc.getFieldName(),
                        new Constraint(pc.getOperator(), pc.getValues())
                    );
                } else {
                    child.getFieldConstraints().put(
                        pc.getFieldName(),
                        new Constraint(pc.getOperator(), pc.getValues().get(0))
                    );
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
}
