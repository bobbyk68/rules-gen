package uk.gov.hmrc.rules.demo;

import java.util.*;

public class XPathDeduplicator {

    /**
     * Controls whether specific predicate attribute values are normalised (stripped)
     * during comparison. If an attribute is not in the map, the default is false (keep the value).
     *
     * true  = normalise (strip the value, treat all values as equivalent)
     * false = keep the value as part of the dedup key
     */
    private final Map<String, Boolean> normalisePredicateValues;

    public XPathDeduplicator(Map<String, Boolean> normalisePredicateValues) {
        this.normalisePredicateValues = normalisePredicateValues;
    }

    /**
     * Deduplicates XPath pointers where paths differ only by sequenceNumber values
     * in the tail (after the goodsItems segment). The first occurrence is always kept.
     */
    public List<String> deduplicate(List<String> xpaths) {
        Set<String> seen = new LinkedHashSet<>();
        List<String> result = new ArrayList<>();

        for (String xpath : xpaths) {
            String normalisedKey = buildNormalisedKey(xpath);
            if (seen.add(normalisedKey)) {
                result.add(xpath); // always keep the original, never the normalised form
            }
        }

        return result;
    }

    /**
     * Builds the normalised key for comparison purposes.
     * The goodsItems segment (with its sequenceNumber) is kept intact as the group key.
     * Everything after goodsItems has its sequenceNumber values stripped.
     */
    private String buildNormalisedKey(String xpath) {
        String[] segments = xpath.split("/");
        StringBuilder key = new StringBuilder();
        boolean pastGoodsItems = false;

        for (String segment : segments) {
            if (segment.isEmpty()) {
                key.append("/");
                continue;
            }

            if (segment.startsWith("goodsItems")) {
                // Keep goodsItems segment intact - it is the group key
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
     * Normalises a single path segment by parsing its predicate char by char.
     * e.g. parties[partyRoleType="LC" and sequenceNumber="1"]
     *   -> parties[partyRoleType="LC" and sequenceNumber]
     */
    private String normaliseSegment(String segment) {
        int bracketStart = segment.indexOf('[');

        if (bracketStart == -1) {
            // No predicate - nothing to normalise
            return segment;
        }

        String nodeName = segment.substring(0, bracketStart);
        String predicateContent = segment.substring(bracketStart + 1, segment.length() - 1);

        String normalisedPredicate = normaliserPredicate(predicateContent);

        return nodeName + "[" + normalisedPredicate + "]";
    }

    /**
     * Parses predicate content char by char, splitting on "and",
     * then normalises each individual condition.
     * e.g. partyRoleType="LC" and sequenceNumber="1"
     *   -> partyRoleType="LC" and sequenceNumber
     */
    private String normaliserPredicate(String predicateContent) {
        List<String> conditions = splitOnAnd(predicateContent);
        List<String> normalisedConditions = new ArrayList<>();

        for (String condition : conditions) {
            normalisedConditions.add(normaliseCondition(condition.trim()));
        }

        return String.join(" and ", normalisedConditions);
    }

    /**
     * Splits predicate content on " and " by walking char by char.
     * This avoids regex and handles the content safely.
     */
    private List<String> splitOnAnd(String predicateContent) {
        List<String> conditions = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int i = 0;

        while (i < predicateContent.length()) {
            // Look ahead for " and "
            if (predicateContent.startsWith(" and ", i)) {
                conditions.add(current.toString());
                current = new StringBuilder();
                i += 5; // skip past " and "
            } else {
                current.append(predicateContent.charAt(i));
                i++;
            }
        }

        if (current.length() > 0) {
            conditions.add(current.toString());
        }

        return conditions;
    }

    /**
     * Normalises a single condition e.g. sequenceNumber="1" or partyRoleType="LC".
     *
     * - sequenceNumber is always normalised (value stripped)
     * - Other attributes are looked up in the normalisePredicateValues map
     *   true  = strip the value
     *   false = keep the value (default if not in map)
     */
    private String normaliseCondition(String condition) {
        // Walk char by char to find the attribute name (everything before '=')
        StringBuilder attributeName = new StringBuilder();
        int i = 0;

        while (i < condition.length() && condition.charAt(i) != '=') {
            attributeName.append(condition.charAt(i));
            i++;
        }

        String attribute = attributeName.toString().trim();

        // sequenceNumber is always normalised - this is the core dedup predicate
        if ("sequenceNumber".equals(attribute)) {
            return attribute; // strip the value entirely
        }

        // Look up in the map - default to false (keep value) if not present
        boolean shouldNormalise = normalisePredicateValues.getOrDefault(attribute, false);

        if (shouldNormalise) {
            return attribute; // strip the value
        } else {
            return condition; // keep full condition e.g. partyRoleType="LC"
        }
    }
}
