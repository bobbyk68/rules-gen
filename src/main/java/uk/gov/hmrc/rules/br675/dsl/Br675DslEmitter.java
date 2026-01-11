package uk.gov.hmrc.rules.br675.dsl;

import uk.gov.hmrc.rules.dsl.*;
import uk.gov.hmrc.rules.ir.*;


public class Br675DslEmitter implements RuleSetDslEmitter {

    private final DslWording wording = new DslWording();
    private final Br675WhenRhsRenderer whenRhs = new Br675WhenRhsRenderer();
    private final Br675ThenRhsRenderer thenRhs = new Br675ThenRhsRenderer();

    @Override
    public boolean supports(String ruleSet) {
        return "BR675".equalsIgnoreCase(ruleSet);
    }

    @Override
    public DslEmission emit(RuleModel model) {
        if (model.getScope() == null) {
            model.setScope(new EmissionScope());
        }
        java.util.List<DslEntry> when = emitWhen(model);
        java.util.List<DslEntry> then = emitThen(model);
        return new DslEmission(when, then);
    }

    @Override
    public java.util.List<DslEntry> emitWhen(RuleModel model) {

        java.util.List<DslEntry> when = new java.util.ArrayList<>();

        // ✅ Use the per-rule scope
        EmissionScope scope = model.getScope();
        if (scope == null) {
            // defensive: in case emitWhen is called directly somewhere
            scope = new EmissionScope();
            model.setScope(scope);
        }

        for (ConditionNode n : model.getConditions()) {
            if (!(n instanceof FactConditionNode fc)) {
                continue;
            }

            String parentAnchorKey = "GI";
            String quantifier = "AT_LEAST_ONE";

            // (A) Anchor DSL entry (exists/bind/join)
            {
                String factType = fc.getFactType();

                boolean isGoodsItem =
                        "GoodsItemFact".equalsIgnoreCase(factType)
                                || "GoodsItem".equalsIgnoreCase(factType);

                String labelForWording = isGoodsItem ? null : fc.getFieldTypeLabel();

                String existenceKey = (fc.getExistence() == FactConditionNode.Existence.NOT_EXISTS)
                        ? "NOT_EXISTS"
                        : "EXISTS";

                String lhs = wording.anchorLhs(fc, quantifier, parentAnchorKey, labelForWording);
                String rhs = whenRhs.renderAnchor(fc, parentAnchorKey);

                System.out.println("ANCHOR RHS: " + rhs);
                System.out.println("SCOPE before size=" + scope.emittedAnchorRhs().size());

                boolean first = scope.markAnchorEmitted(rhs);

                System.out.println("SCOPE after  size=" + scope.emittedAnchorRhs().size() + " first=" + first);


                // ✅ scope-driven dedupe (replaces local Set)
                if (scope.markAnchorEmitted(rhs)) {
                    when.add(new DslEntry(
                            new DslKey("BR675", "condition", quantifier, parentAnchorKey,
                                    factType, "__ANCHOR__", existenceKey),
                            "condition",
                            lhs,
                            rhs
                    ));
                }
            }

            // (B) Dash DSL entries (constraints)
            for (java.util.Map.Entry<String, Constraint> e : fc.getFieldConstraints().entrySet()) {
                String field = e.getKey();
                Constraint c = e.getValue();

                String op = normaliseOperator(c.getOperator());

                String displayField;
                boolean isGoodsItem =
                        "GoodsItem".equalsIgnoreCase(fc.getFactType())
                                || "GoodsItemFact".equalsIgnoreCase(fc.getFactType());

                if (isGoodsItem) {
                    displayField = switch (field) {
                        case "requestedProcedureCode" -> "requested procedure code";
                        case "previousProcedureCode"  -> "previous procedure code";
                        default -> field;
                    };
                } else {
                    String label = fc.getFieldTypeLabel();
                    displayField = (label == null || label.isBlank()) ? field : (label + " " + field);
                }

                final String lhs;
                final String rhs;

                if (isUnary(op)) {
                    lhs = unaryDashLhs(displayField, op);

                    rhs = switch (op) {
                        case "IS_PROVIDED"     -> field + " != null";
                        case "IS_NOT_PROVIDED" -> field + " == null";
                        default -> throw new IllegalStateException("Unsupported unary op: " + op);
                    };
                } else {
                    lhs = wording.dashLhs(displayField, op);
                    rhs = whenRhs.renderFieldConstraint(fc, field, op);
                }

                when.add(new DslEntry(
                        new DslKey("BR675", "condition", quantifier, parentAnchorKey,
                                fc.getFactType(), field, op),
                        "condition",
                        lhs,
                        rhs
                ));
            }



        }

        System.out.println("WHEN entries count=" + when.size());
        for (DslEntry e : when) {
            System.out.println("WHEN LHS=" + e.lhs());
        }


        return when;
    }


    @Override
    public java.util.List<DslEntry> emitThen(RuleModel model) {

        java.util.List<DslEntry> then = new java.util.ArrayList<>();

        for (ActionNode a : model.getActions()) {
            if (!(a instanceof EmitErrorActionNode emit)) {
                continue;
            }

            String lhs = wording.emitLhs(emit.getBrCode());
            String rhs = thenRhs.renderEmit(emit);

            then.add(new DslEntry(
                    new DslKey("BR675", "consequence", "EMIT", "DECL",
                            "ValidationResult", "errorCode", "=="),
                    "consequence",
                    lhs,
                    rhs
            ));
        }

        return then;
    }



    /**
     * Normalise operators into a small stable set for DSL keys/phrasing.
     * Adjust mapping as your Constraint operators become clearer.
     */
    private static String normaliseOperator(String op) {
        if (op == null) return "==";

        String o = op.trim();
        if (o.isEmpty()) return "==";

        // Common possibilities from your earlier work
        if (o.equals("=") || o.equals("==") || o.equalsIgnoreCase("EQUALS")) return "==";
        if (o.equals("!=") || o.equalsIgnoreCase("NOT_EQUALS")) return "!=";
        if (o.equalsIgnoreCase("IN")) return "IN";
        if (o.equalsIgnoreCase("NOT_IN")) return "NOT_IN";
        if (o.equals(">") || o.equalsIgnoreCase("GT")) return ">";
        if (o.equals(">=") || o.equalsIgnoreCase("GTE")) return ">=";
        if (o.equals("<") || o.equalsIgnoreCase("LT")) return "<";
        if (o.equals("<=") || o.equalsIgnoreCase("LTE")) return "<=";

        return o.toUpperCase();
    }

    private static boolean isUnary(String op) {
        if (op == null) return false;
        return switch (op.toUpperCase()) {
            case "IS_PROVIDED", "IS_NOT_PROVIDED" -> true;
            default -> false;
        };
    }

    private String unaryDashLhs(String displayField, String op) {
        // Keep DSL keys human and stable. No {value}.
        return switch (op.toUpperCase()) {
            case "IS_PROVIDED"     -> "- with " + displayField + " provided";
            case "IS_NOT_PROVIDED" -> "- with " + displayField + " not provided";
            default -> "- with " + displayField + " " + op;
        };
    }

}

