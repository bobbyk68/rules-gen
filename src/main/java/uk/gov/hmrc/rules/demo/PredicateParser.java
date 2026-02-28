package uk.gov.hmrc.rules.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Responsible for parsing and normalising XPath predicate content.
 *
 * A predicate is the content inside [...] in an XPath segment.
 * e.g. partyRoleType="LC" and sequenceNumber="1"
 *
 * Handles:
 * - Splitting compound predicates on " and "
 * - Normalising individual conditions based on the attribute name
 * - sequenceNumber is always normalised (value stripped)
 * - Other attributes are looked up in the normalisePredicateValues map
 *
 * @version 0.1.0-SNAPSHOT
 * @since 2026-02-27
 */
class PredicateParser {

    /**
     * Controls whether specific predicate attribute values are normalised (stripped).
     * true  = normalise (strip the value)
     * false = keep the value (default if not in map)
     */
    private final Map<String, Boolean> normalisePredicateValues;

    PredicateParser(Map<String, Boolean> normalisePredicateValues) {
        this.normalisePredicateValues = normalisePredicateValues;
    }

    /**
     * Normalises a full predicate content string.
     * e.g. partyRoleType="LC" and sequenceNumber="1"
     *   -> partyRoleType="LC" and sequenceNumber
     */
    String normalise(String predicateContent) {
        List<String> conditions = splitOnAnd(predicateContent);
        List<String> normalisedConditions = new ArrayList<>();

        for (String condition : conditions) {
            normalisedConditions.add(normaliseCondition(condition.trim()));
        }

        return String.join(" and ", normalisedConditions);
    }

    /**
     * Splits predicate content on " and " by walking char by char.
     * e.g. partyRoleType="LC" and sequenceNumber="1"
     *   -> ["partyRoleType=\"LC\"", "sequenceNumber=\"1\""]
     */
    private List<String> splitOnAnd(String predicateContent) {
        List<String> conditions = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int i = 0;

        while (i < predicateContent.length()) {
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
     * Normalises a single condition by extracting the attribute name char by char
     * and deciding whether to strip the value based on the map.
     *
     * - sequenceNumber is always normalised (value stripped)
     * - Other attributes looked up in normalisePredicateValues map
     *   true  = strip the value
     *   false = keep the value (default if not in map)
     */
    private String normaliseCondition(String condition) {
        StringBuilder attributeName = new StringBuilder();
        int i = 0;

        while (i < condition.length() && condition.charAt(i) != '=') {
            attributeName.append(condition.charAt(i));
            i++;
        }

        String attribute = attributeName.toString().trim();

        if ("sequenceNumber".equals(attribute)) {
            return attribute;
        }

        boolean shouldNormalise = normalisePredicateValues.getOrDefault(attribute, false);
        return shouldNormalise ? attribute : condition;
    }
}
