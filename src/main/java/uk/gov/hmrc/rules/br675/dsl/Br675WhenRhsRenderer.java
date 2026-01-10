package uk.gov.hmrc.rules.br675.dsl;

import uk.gov.hmrc.rules.br675.registry.Br675FactDslRegistry;
import uk.gov.hmrc.rules.dsl.FactDslRegistry;

public class Br675WhenRhsRenderer {

    private final Br675FactDslRegistry registry = new Br675FactDslRegistry();

    public String renderAnchor(uk.gov.hmrc.rules.ir.FactConditionNode fc, String parentAnchorKey) {

        FactDslRegistry.Resolved resolved = registry.resolve(fc);
        FactDslRegistry.AnchorSpec spec = resolved.anchor();

        boolean negated =
                fc.getExistence() == uk.gov.hmrc.rules.ir.FactConditionNode.Existence.NOT_EXISTS;

        // ---- Alias override (dev-style $sp_match etc.) ----
        String alias = spec.alias();

        boolean isSecondary = fc.getRole() == uk.gov.hmrc.rules.parsing.ConditionRole.SECONDARY;

        // Only do this for anchors that are "special procedure" child facts
        boolean isSpecialProcedureFact =
                "GoodsItemSpecialProcedureTypeFact".equals(spec.factClass());

        if (isSecondary && isSpecialProcedureFact) {
            alias = "$sp_match";
        }
        // -----------------------------------------------

        if (!negated) {
            // Binder: binds $giSeq from the fact
            return alias + ": " + spec.factClass() + "(\n"
                    + "    " + spec.seqVar() + ": " + spec.bindField() + "\n"
                    + ")";
        }

        // Negated/joined: constrains child fact using join field == $giSeq
        return "not " + spec.factClass() + "(\n"
                + "    " + spec.joinField() + " == " + spec.seqVar() + "\n"
                + ")";
    }



    /**
     * Dash RHS: bible style constraint-only.
     * Example in your DSL:
     *   code == {value}
     *
     * NOTE: no quotes, no commas, no brackets.
     */
    public String renderFieldConstraint(uk.gov.hmrc.rules.ir.FactConditionNode fc, String field, String op) {
        return safe(field) + " " + normaliseOperator(op) + " {value}";
    }

    // ==================================================
    // Local mapping: IR concept -> domain fact class + alias
    // Extend this as you add more BR675 child fact types.
    // ==================================================

    private static String normaliseOperator(String op) {
        if (op == null) return "==";
        String o = op.trim();
        if (o.isEmpty()) return "==";

        if (o.equals("=") || o.equals("==") || o.equalsIgnoreCase("EQUALS")) return "==";
        if (o.equals("!=") || o.equalsIgnoreCase("NOT_EQUALS")) return "!=";

        return o.toUpperCase();
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }


}
