package uk.gov.hmrc.rules.dsl;

import uk.gov.hmrc.rules.ir.Constraint;
import uk.gov.hmrc.rules.ir.FactConditionNode;

public class Br675WhenRhsRenderer {

    /**
     * Anchor RHS: emit the same "bible" DRL shapes you showed.
     *
     * EXISTS example:
     *   $sp: GoodsItemSpecialProcedureTypeFact(
     *       $giSeq: goodsItemSequenceNumber
     *   )
     *
     * NOT_EXISTS example:
     *   not GoodsItemSpecialProcedureTypeFact(
     *       goodsItemSequenceNumber == $giSeq
     *   )
     */
    public String renderAnchor(uk.gov.hmrc.rules.ir.FactConditionNode fc, String parentAnchorKey) {

        FactSpec spec = resolveFactSpec(fc);


        boolean negated =
                fc.getExistence() == uk.gov.hmrc.rules.ir.FactConditionNode.Existence.NOT_EXISTS;

        if (!negated) {
            // "exists" form binds $giSeq from the fact (exactly like your screenshot)
            return spec.alias + ": " + spec.factClass + "(\n"
                    + "    " + spec.seqVar + ": " + spec.seqField + "\n"
                    + ")";
        }

        // "no matching" form references $giSeq (again like your screenshot)
        return "not " + spec.factClass + "(\n"
                + "    " + spec.seqField + " == " + spec.seqVar + "\n"
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
    private FactSpec resolveFactSpec(uk.gov.hmrc.rules.ir.FactConditionNode fc) {

        String label = safe(fc.getFieldTypeLabel()).toLowerCase();

        // Matches your screenshot
        if (label.contains("special procedure")) {
            return new FactSpec("$sp", "GoodsItemSpecialProcedureTypeFact",
                    "$giSeq", "goodsItemSequenceNumber");
        }

        // Likely BR675 pair (adjust the fact class name to your bible DSL file)
        if (label.contains("additional information")) {
            return new FactSpec("$ai", "GoodsItemAdditionalInformationFact",
                    "$giSeq", "goodsItemSequenceNumber");
        }

        // Fallback so you can immediately see unmapped concepts in output
        return new FactSpec(
                safe(fc.getAlias()).isBlank() ? "$c" : safe(fc.getAlias()),
                safe(fc.getFactType()).isBlank() ? "UNKNOWN_FACT_TYPE" : safe(fc.getFactType()),
                "$giSeq",
                // fallback to your IR join name so it still “makes sense” in debug
                safe(fc.getParentJoinField()).isBlank() ? "parentSeq" : safe(fc.getParentJoinField())
        );
    }

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

    private static class FactSpec {
        final String alias;
        final String factClass;
        final String seqVar;
        final String seqField;

        FactSpec(String alias, String factClass, String seqVar, String seqField) {
            this.alias = alias;
            this.factClass = factClass;
            this.seqVar = seqVar;
            this.seqField = seqField;
        }
    }
}
