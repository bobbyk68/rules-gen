package uk.gov.hmrc.rules.demo;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for parsing and normalising XPath predicate content.
 *
 * A predicate is the content inside [...] in an XPath segment.
 * e.g. partyRoleType="LC" and sequenceNumber="1"
 *
 * Handles:
 * - Splitting compound predicates on " and " char by char
 * - Normalising individual conditions based on NormalisationConfig
 * - sequenceNumber is always normalised regardless of config
 * - Other attributes delegated to NormalisationConfig
 *
 * @version 0.2.0-SNAPSHOT
 * @since 2026-02-27
 */
class PredicateParser {

    private final NormalisationConfig config;

    PredicateParser(NormalisationConfig config) {
        this.config = config;
    }

    String normalise(String predicateContent) {
        List<String> conditions = splitOnAnd(predicateContent);
        List<String> normalisedConditions = new ArrayList<>();
        for (String condition : conditions) {
            normalisedConditions.add(normaliseCondition(condition.trim()));
        }
        return String.join(" and ", normalisedConditions);
    }

    private List<String> splitOnAnd(String predicateContent) {
        List<String> conditions = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int i = 0;
        while (i < predicateContent.length()) {
            if (predicateContent.startsWith(" and ", i)) {
                conditions.add(current.toString());
                current = new StringBuilder();
                i += 5;
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
        return config.shouldNormalise(attribute) ? attribute : condition;
    }
}
