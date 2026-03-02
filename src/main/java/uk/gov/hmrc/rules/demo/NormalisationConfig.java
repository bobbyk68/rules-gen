package uk.gov.hmrc.rules.demo;

import java.util.Map;

/**
 * Owns the configuration that drives predicate attribute normalisation during XPath deduplication.
 *
 * Each entry in the map represents a predicate attribute name and whether its value
 * should be normalised (stripped) when building a deduplication key.
 *
 * true  = normalise - strip the value, treat all values as equivalent
 * false = keep the value as part of the dedup key (default for unknown attributes)
 *
 * Note: sequenceNumber is always normalised regardless of this configuration.
 * It is the core predicate that drives deduplication and is hardcoded in PredicateParser.
 *
 * Example configuration:
 *   partyRoleType         -> false (LC and CN are distinct, never dedupe across them)
 *   countryRegionRoleType -> true  (value does not affect dedup identity)
 *
 * @version 0.2.0-SNAPSHOT
 * @since 2026-03-02
 */
public class NormalisationConfig {

    private final Map<String, Boolean> predicateMap;

    public NormalisationConfig(Map<String, Boolean> predicateMap) {
        this.predicateMap = predicateMap;
    }

    /**
     * Returns whether the given predicate attribute value should be normalised.
     * Defaults to false (keep the value) if the attribute is not in the map.
     *
     * @param attributeName the predicate attribute name e.g. partyRoleType
     * @return true if the value should be stripped, false if it should be kept
     */
    public boolean shouldNormalise(String attributeName) {
        return predicateMap.getOrDefault(attributeName, false);
    }

    /**
     * Returns the underlying map for use where the full map is needed.
     *
     * @return unmodifiable view of the predicate normalisation map
     */
    public Map<String, Boolean> getMap() {
        return Map.copyOf(predicateMap);
    }
}
