package uk.gov.hmrc.rules.dsl;

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
        java.util.List<DslEntry> when = emitWhen(model);
        java.util.List<DslEntry> then = emitThen(model);
        return new DslEmission(when, then);
    }

    @Override
    public java.util.List<DslEntry> emitWhen(RuleModel model) {

        java.util.List<DslEntry> when = new java.util.ArrayList<>();

        for (ConditionNode n : model.getConditions()) {
            if (!(n instanceof FactConditionNode fc)) {
                continue;
            }

            // TODO (later): carry these on the IR nodes rather than infer.
            String parentAnchorKey = "GI";
            String quantifier = "AT_LEAST_ONE";

            // (A) Anchor DSL entry (exists/bind/join)
            {
                String lhs = wording.anchorLhs(fc, quantifier, parentAnchorKey, fc.getFieldTypeLabel());

                String rhs = whenRhs.renderAnchor(fc, parentAnchorKey);

                when.add(new DslEntry(
                        new DslKey("BR675", "condition", quantifier, parentAnchorKey,
                                fc.getFactType(), "__ANCHOR__", "EXISTS"),
                        "condition",
                        lhs,
                        rhs
                ));
            }

            // (B) Dash DSL entries (constraints)
            for (java.util.Map.Entry<String, Constraint> e : fc.getFieldConstraints().entrySet()) {
                String field = e.getKey();
                Constraint c = e.getValue();

                String op = normaliseOperator(c.getOperator());

                String lhs = wording.dashLhs(field, op);
                String rhs = whenRhs.renderFieldConstraint(fc, field, op);

                when.add(new DslEntry(
                        new DslKey("BR675", "condition", quantifier, parentAnchorKey,
                                fc.getFactType(), field, op),
                        "condition",
                        lhs,
                        rhs
                ));
            }
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
}

