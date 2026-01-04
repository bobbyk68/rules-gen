package uk.gov.hmrc.rules.dsl;

import uk.gov.hmrc.rules.ir.Constraint;
import uk.gov.hmrc.rules.ir.FactConditionNode;

public class Br675WhenRhsRenderer {

    private final Br675FactDslRegistry registry = new Br675FactDslRegistry();

    /**
     * Anchor RHS: bible-aligned domain facts.
     *
     * EXISTS example (your target):
     *   $sp : GoodsItemSpecialProcedureTypeFact( $giSeq : goodsItemSequenceNumber )
     *
     * NOT_EXISTS example:
     *   not( GoodsItemAdditionalInformationFact( goodsItemSequenceNumber == $giSeq ) )
     */
    public String renderAnchor(uk.gov.hmrc.rules.ir.FactConditionNode fc, String parentAnchorKey) {

        Br675FactDslRegistry.FactSpec spec = registry.resolve(fc);

        boolean negated =
                fc.getExistence() == uk.gov.hmrc.rules.ir.FactConditionNode.Existence.NOT_EXISTS;

        if (!negated) {
            // Bind the sequence variable from the fact (matches your screenshot style)
            return spec.dslAlias() + " : " + spec.droolsFactType()
                    + "( " + spec.seqVar() + " : " + spec.seqFieldName() + " )";
        }

        // For “no matching … exists”, reference the already-bound $giSeq
        return "not( " + spec.droolsFactType()
                + "( " + spec.seqFieldName() + " == " + spec.seqVar() + " ) )";
    }

    /**
     * Constraint RHS: bible style (constraint-only).
     *
     * Example:
     *   code == {value}
     *
     * NOTE: Your bible DSL does not quote {value} here.
     * Keep it unquoted to match.
     */
    public String renderFieldConstraint(uk.gov.hmrc.rules.ir.FactConditionNode fc, String field, String op) {
        return safe(field) + " " + normaliseOperator(op) + " {value}";
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
}
