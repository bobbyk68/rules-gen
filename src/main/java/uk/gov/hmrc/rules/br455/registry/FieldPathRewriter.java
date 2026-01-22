package uk.gov.hmrc.rules.br455.registry;

import java.util.Map;

public final class FieldPathRewriter {

    // 1) Segment-level renames (applied to individual path segments)
    // key: factClassSimpleName (or "DEFAULT:GoodsItem" etc)
    private final Map<String, Map<String, String>> segmentRenamesByFact;

    // 2) Tail-level rewrites (applied to the whole bindPath, suffix match)
    // key: factClassSimpleName
    private final Map<String, Map<String, String>> tailRewritesByFact;

    public FieldPathRewriter() {
        this.segmentRenamesByFact = Map.of(
                // Example: only if you truly have a leaf segment mismatch
                "GoodsItemFact", Map.of(
                        "investmentType", "type"
                )
        );

        this.tailRewritesByFact = Map.of(
                // Example: previousDocuments facts often flatten type.code -> typeCode
                "GoodsItemPreviousDocumentFact", Map.of(
                        "type.code", "typeCode",
                        "category.code", "category"
                ),
                "ConsignmentShipmentPreviousDocumentFact", Map.of(
                        "type.code", "typeCode",
                        "category.code", "category"
                ),
                // Example: transport means often have mode and type flattened
//                "ConsignmentShipmentFact", Map.of(
//                        // ONLY if your ConsignmentShipmentFact actually needs these
//                        // "mode.code", "mode",
//                        // "identificationType.code", "identificationType"
//                        ""
//                )
        );
    }

    /**
     * Rewrite AFTER routing.
     * @param factClassSimpleName resolved fact class (e.g. "GoodsItemPreviousDocumentFact")
     * @param segment1 original spreadsheet segment1 (useful if you want later)
     * @param bindPath the drools property path starting at startIndex (e.g. "type.code")
     */
    public String rewriteBindPath(String factClassSimpleName, String segment1, String bindPath) {
        if (bindPath == null || bindPath.isBlank()) return bindPath;

        String out = bindPath;

        // Tier 2 first: full tail rewrites (most specific)
        Map<String, String> tailMap = tailRewritesByFact.get(factClassSimpleName);
        if (tailMap != null && !tailMap.isEmpty()) {
            out = applyTailRewrite(tailMap, out);
        }

        // Tier 1 second: segment renames (safe global cleanup)
        Map<String, String> segMap = segmentRenamesByFact.get(factClassSimpleName);
        if (segMap != null && !segMap.isEmpty()) {
            out = applySegmentRenames(segMap, out);
        }

        return out;
    }

    private String applySegmentRenames(Map<String, String> renames, String bindPath) {
        String[] segs = bindPath.split("\\.");
        for (int i = 0; i < segs.length; i++) {
            segs[i] = renames.getOrDefault(segs[i], segs[i]);
        }
        return String.join(".", segs);
    }

    private String applyTailRewrite(Map<String, String> tailRewrites, String bindPath) {
        // Prefer longest suffix match wins (e.g. "transportMeansIdentificationType.code" before "code")
        String bestKey = null;
        for (String k : tailRewrites.keySet()) {
            if (k == null || k.isBlank()) continue;
            if (bindPath.equals(k) || bindPath.endsWith("." + k)) {
                if (bestKey == null || k.length() > bestKey.length()) {
                    bestKey = k;
                }
            }
        }
        if (bestKey == null) return bindPath;

        String replacement = tailRewrites.get(bestKey);
        if (bindPath.equals(bestKey)) return replacement;

        // suffix replace: prefix + "." + replacement
        int cut = bindPath.length() - bestKey.length();
        String prefix = bindPath.substring(0, cut);
        if (prefix.endsWith(".")) prefix = prefix.substring(0, prefix.length() - 1);
        return prefix.isEmpty() ? replacement : prefix + "." + replacement;
    }
}

