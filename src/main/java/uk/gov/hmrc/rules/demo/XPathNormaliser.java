package uk.gov.hmrc.rules.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for normalising an XPath string into a deduplication key,
 * and extracting sequence numbers from the tail for sorting purposes.
 *
 * The normalised key:
 * - Preserves the goodsItems segment intact (including its sequenceNumber) as the grouping key
 * - Strips sequenceNumber values from all segments after goodsItems
 * - Applies NormalisationConfig-driven normalisation to other predicate attributes
 *
 * e.g.
 * /declaration/consignmentShipment/goodsItems[sequenceNumber="1"]/parties[partyRoleType="LC" and sequenceNumber="2"]/partyIdentification
 * ->
 * /declaration/consignmentShipment/goodsItems[sequenceNumber="1"]/parties[partyRoleType="LC" and sequenceNumber]/partyIdentification
 *
 * @version 0.2.0-SNAPSHOT
 * @since 2026-02-27
 */
class XPathNormaliser {

    private static final Pattern SEQUENCE_NUMBER_VALUE = Pattern.compile("sequenceNumber=\"(\\d+)\"");

    private final PredicateParser predicateParser;

    XPathNormaliser(NormalisationConfig config) {
        this.predicateParser = new PredicateParser(config);
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
                key.append(segment);
                pastGoodsItems = true;
            } else if (pastGoodsItems) {
                key.append(normaliseSegment(segment));
            } else {
                key.append(segment);
            }
            key.append("/");
        }

        if (key.length() > 0 && key.charAt(key.length() - 1) == '/') {
            key.deleteCharAt(key.length() - 1);
        }

        return key.toString();
    }

    /**
     * Extracts all sequenceNumber values from the tail of the xpath (after goodsItems).
     * Used by LowestSequenceNumberStrategy to sort candidates.
     * Uses regex for extraction as we only need the values, not to manipulate the string.
     *
     * e.g. goodsItems[sequenceNumber="1"]/parties[partyRoleType="LC" and sequenceNumber="3"]/partyIdentification
     *   -> [3]
     *
     * @param xpath the raw xpath string
     * @return ordered list of sequence number integers from the tail
     */
    List<Integer> extractSequenceNumbers(String xpath) {
        List<Integer> sequenceNumbers = new ArrayList<>();

        // Find the goodsItems boundary first - only extract from tail
        int goodsItemsEnd = xpath.indexOf(']', xpath.indexOf("goodsItems["));
        if (goodsItemsEnd == -1) {
            return sequenceNumbers;
        }

        String tail = xpath.substring(goodsItemsEnd + 1);

        Matcher matcher = SEQUENCE_NUMBER_VALUE.matcher(tail);
        while (matcher.find()) {
            sequenceNumbers.add(Integer.parseInt(matcher.group(1)));
        }

        return sequenceNumbers;
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
