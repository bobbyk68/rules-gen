package uk.gov.hmrc.rules.br675.registry;

import uk.gov.hmrc.rules.dsl.FactDslRegistry;

public class Br675FactDslRegistry implements FactDslRegistry {

    /**
     * Minimal “bible-aligned” mapping.
     *
     * NOTE: Adjust the keys + fact class names to match your real domain classes.
     * This is intentionally explicit and boring.
     */
    public Resolved resolve(uk.gov.hmrc.rules.ir.FactConditionNode fc) {

        String label = safe(fc.getFieldTypeLabel()).toLowerCase();

        // Default dash (you can evolve this later based on operator/fieldName)
        DashSpec dash = new DashSpec(
                "- with code == {value}",
                "code == {value}"
        );

        if ("GoodsItem".equals(safe(fc.getFactType()))) {

            dash = new DashSpec(
                    "- {field} == {value}",     // or whatever default you want
                    "{field} == {value}"
            );

            AnchorSpec anchor = new AnchorSpec(
                    "Goods item exists",
                    "$g1: GoodsItemFact( $giSeq : sequenceNumber )",
                    "$g1",
                    "GoodsItemFact",
                    "$giSeq",
                    "goodsItemSequenceNumber",          // bindField on GoodsItemFact
                    "goodsItemSequenceNumber"           // joinField (if you ever need it)
            );

            return new Resolved(anchor, dash);
        }


        // Matches your screenshot example
        if (label.contains("special procedure")) {
            AnchorSpec anchor = new AnchorSpec(
                    "Goods item with special procedure exists",
                    "...",
                    "$sp",
                    "GoodsItemSpecialProcedureTypeFact",
                    "$giSeq",
                    "goodsItemSequenceNumber",   // bindField
                    "goodsItemSequenceNumber"    // joinField (same for now)
            );

            return new Resolved(anchor, dash);
        }

        // Additional information anchor (used by the NOT EXISTS anchor in your output)
        if (label.contains("additional information")) {
            AnchorSpec anchor = new AnchorSpec(
                    "No matching goods item with additional information exists",
                    "...",
                    "$giAi",
                    "GoodsItemAdditionalInformationFact",
                    "$giSeq",
                    "goodsItemSequenceNumber",   // bindField (unused in NOT_EXISTS path)
                    "goodsItemSequenceNumber"    // joinField (this is the key thing)
            );

            return new Resolved(anchor, dash);
        }

        if (label.contains("additional document")) {
            AnchorSpec anchor = new AnchorSpec(
                    "No matching goods item with additional document exists",
                    "...",
                    "$giAdDoc",
                    "GoodsItemAdditionalDocumentFact",
                    "$giSeq",
                    "goodsItemSequenceNumber",   // bindField (unused in NOT_EXISTS path)
                    "goodsItemSequenceNumber"    // joinField (this is the key thing)
            );

            return new Resolved(anchor, dash);
        }

        // Fallback so you can see what wasn’t mapped yet
        AnchorSpec fallback = new AnchorSpec(
                "UNMAPPED anchor exists",
                (safe(fc.getFactType()).isBlank() ? "UNKNOWN_FACT_TYPE" : safe(fc.getFactType())) + "( ... )",
                safe(fc.getAlias()).isBlank() ? "$c" : safe(fc.getAlias()),
                safe(fc.getFactType()).isBlank() ? "UNKNOWN_FACT_TYPE" : safe(fc.getFactType()),
                "$giSeq",
                "goodsItemSequenceNumber",
                "parentSeq"
        );


        return new Resolved(fallback, dash);
    }


    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
