package uk.gov.hmrc.rules.dsl;

public class Br675WhenRhsRenderer {

    /**
     * Anchor RHS: emit the same "bible" DRL shapes you showed.
     * <p>
     * EXISTS example:
     * $sp: GoodsItemSpecialProcedureTypeFact(
     * $giSeq: goodsItemSequenceNumber
     * )
     * <p>
     * NOT_EXISTS example:
     * not GoodsItemSpecialProcedureTypeFact(
     * goodsItemSequenceNumber == $giSeq
     * )
     */
    public String renderAnchor(uk.gov.hmrc.rules.ir.FactConditionNode fc, String parentAnchorKey) {

        Br675FactDslRegistry.FactSpec spec = new Br675FactDslRegistry().resolve(fc);


        boolean negated =
                fc.getExistence() == uk.gov.hmrc.rules.ir.FactConditionNode.Existence.NOT_EXISTS;

        if (!negated) {
            // "exists" form binds $giSeq from the fact (exactly like your screenshot)
            return spec.alias() + ": " + spec.factClass() + "(\n"
                    + "    " + spec.seqVar() + ": " + spec.seqField() + "\n"
                    + ")";
        }

        // "no matching" form references $giSeq (again like your screenshot)
        return "not " + spec.factClass() + "(\n"
                + "    " + spec.seqField() + " == " + spec.seqVar() + "\n"
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
