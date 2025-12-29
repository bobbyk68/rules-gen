package uk.gov.hmrc.rules.ir;

public final class MergeWhitelistPolicy implements IrPolicy {

    private final java.util.Set<String> mergePairs = new java.util.HashSet<>();

    public MergeWhitelistPolicy() {
        // Canonical pair key format: "<ANCHOR>|<ENTITY>|<FIELD>"
        allow("GI|GoodsItem|requestedProcedureCode", "GI|GoodsItem|previousProcedureCode");
        allow("GI|AdditionalDocument|exemptionCode", "GI|AdditionalDocument|typeCode");
        allow("GI|ValuationAdjustment|typeCode", "GI|ValuationAdjustment|amountValue");
    }


    private void allow(String ifKey, String thenKey) {
        mergePairs.add(pair(ifKey, thenKey));
        // also allow reversed order, just in case
        mergePairs.add(pair(thenKey, ifKey));
    }

    private String pair(String a, String b) {
        return a.trim().toLowerCase(java.util.Locale.ROOT) + "||" + b.trim().toLowerCase(java.util.Locale.ROOT);
    }

    @Override
    public MergeDecision decideMerge(uk.gov.hmrc.rules.parsing.ParsedCondition ifCondition,
                                     uk.gov.hmrc.rules.parsing.ParsedCondition thenCondition) {

        String ifKey = normalise(ifCondition.canonicalPathKey());
        String thKey = normalise(thenCondition.canonicalPathKey());

        return mergePairs.contains(pair(ifKey, thKey))
                ? MergeDecision.MERGE
                : MergeDecision.NOT_MERGE;
    }

    private String normalise(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
