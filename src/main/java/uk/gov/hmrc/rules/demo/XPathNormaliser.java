package uk.gov.hmrc.rules.demo;

import java.util.Map;

/**
 * Responsible for normalising an XPath string into a deduplication key.
 *
 * The normalised key:
 * - Preserves the goodsItems segment intact (including its sequenceNumber) as the grouping key
 * - Strips sequenceNumber values from all segments after goodsItems
 * - Applies map-driven normalisation to other predicate attributes via PredicateParser
 *
 * e.g.
 * /declaration/consignmentShipment/goodsItems[sequenceNumber="1"]/parties[partyRoleType="LC" and sequenceNumber="2"]/partyIdentification
 * ->
 * /declaration/consignmentShipment/goodsItems[sequenceNumber="1"]/parties[partyRoleType="LC" and sequenceNumber]/partyIdentification
 *
 * @version 0.1.0-SNAPSHOT
 * @since 2026-02-27
 */
class XPathNormaliser {

    private final PredicateParser predicateParser;

    XPathNormaliser(Map<String, Boolean> normalisePredicateValues) {
        this.predicateParser = new PredicateParser(normalisePredicateValues);
    }

    /**
     * Builds a normalised key from a raw xpath string for deduplication comparison.
     * The original xpath string is never modified - the key is a throwaway comparison token.
     */
    String buildNormalisedKey(String xpath) {
        String[] segments = xpath.split("/");
        StringBuilder key = new StringBuilder();
        boolean pastGoodsItems = false;

        for (String segment : segments) {
            if (segment.isEmpty()) {
                key.append("/");
                continue;
            }

            if (segment.startsWith("goodsItems")) {
                // Keep goodsItems segment intact - it is the grouping key
                key.append(segment);
                pastGoodsItems = true;
            } else if (pastGoodsItems) {
                // Normalise everything after goodsItems
                key.append(normaliseSegment(segment));
            } else {
                // Before goodsItems - keep as-is (e.g. declaration, consignmentShipment)
                key.append(segment);
            }

            key.append("/");
        }

        // Remove trailing slash
        if (key.length() > 0 && key.charAt(key.length() - 1) == '/') {
            key.deleteCharAt(key.length() - 1);
        }

        return key.toString();
    }

    /**
     * Normalises a single path segment by extracting and normalising its predicate.
     * e.g. parties[partyRoleType="LC" and sequenceNumber="1"]
     *   -> parties[partyRoleType="LC" and sequenceNumber]
     */
    private String normaliseSegment(String segment) {
        int bracketStart = segment.indexOf('[');

        if (bracketStart == -1) {
            return segment;
        }

        String nodeName = segment.substring(0, bracketStart);
        String predicateContent = segment.substring(bracketStart + 1, segment.length() - 1);
        String normalisedPredicate = predicateParser.normalise(predicateContent);

        return nodeName + "[" + normalisedPredicate + "]";
    }
}
