package uk.gov.hmrc.rules.dsl;

public class Br675FactDslRegistry {

    public record FactSpec(
            String dslAlias,          // "$sp"
            String droolsFactType,    // "GoodsItemSpecialProcedureTypeFact"
            String seqVar,            // "$giSeq"
            String seqFieldName       // "goodsItemSequenceNumber"
    ) {}

    /**
     * Minimal “bible-aligned” mapping.
     *
     * NOTE: Adjust the keys + fact class names to match your real domain classes.
     * This is intentionally explicit and boring.
     */
    public FactSpec resolve(uk.gov.hmrc.rules.ir.FactConditionNode fc) {

        String label = safe(fc.getFieldTypeLabel()).toLowerCase();

        // Matches your screenshot example
        if (label.contains("special procedure")) {
            return new FactSpec(
                    "$sp",
                    "GoodsItemSpecialProcedureTypeFact",
                    "$giSeq",
                    "goodsItemSequenceNumber"
            );
        }

        // Likely name — adjust to your actual class name in the bible DSL
        if (label.contains("additional information")) {
            return new FactSpec(
                    "$ai",
                    "GoodsItemAdditionalInformationFact",
                    "$giSeq",
                    "goodsItemSequenceNumber"
            );
        }

        // Fallback so you can see what wasn’t mapped yet
        return new FactSpec(
                safe(fc.getAlias()).isBlank() ? "$c" : safe(fc.getAlias()),
                safe(fc.getFactType()).isBlank() ? "UNKNOWN_FACT_TYPE" : safe(fc.getFactType()),
                "$giSeq",
                "parentSeq"
        );
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }
}
