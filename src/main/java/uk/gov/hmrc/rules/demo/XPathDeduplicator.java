package uk.gov.hmrc.rules.demo;

import java.util.*;

/**
 * Orchestrates deduplication of XPath pointers across a list of RuleFiredValidationResult objects.
 *
 * Delegates all XPath normalisation to XPathNormaliser.
 *
 * Rules:
 * - Order of results is preserved throughout
 * - Dedup is scoped per rule ID - pointers are only compared within the same rule
 * - First occurrence of a normalised pointer within a rule is kept
 * - Duplicate pointers are removed directly from the mutable pointers list on each result
 * - If a result ends up with no pointers after dedup, it is excluded from the output
 * - The input list is never modified - a new output list is built
 *
 * @version 0.1.0-SNAPSHOT
 * @since 2026-02-27
 */
public class XPathDeduplicator {

    private final XPathNormaliser normaliser;

    public XPathDeduplicator(Map<String, Boolean> normalisePredicateValues) {
        this.normaliser = new XPathNormaliser(normalisePredicateValues);
    }

    /**
     * Deduplicates pointers across a list of RuleFiredValidationResult objects.
     *
     * @param results list of validation results to deduplicate
     * @return new list in original order, containing only results that still have pointers
     */
    public List<RuleFiredValidationResult> deduplicate(List<RuleFiredValidationResult> results) {
        // One seen set per rule ID - scopes dedup within each rule
        Map<String, Set<String>> seenByRule = new HashMap<>();
        List<RuleFiredValidationResult> output = new ArrayList<>();

        for (RuleFiredValidationResult result : results) {
            String ruleId = result.getRule().getId();

            // Get or create the seen set for this rule
            Set<String> seen = seenByRule.computeIfAbsent(ruleId, k -> new HashSet<>());

            // Walk the pointers list, removing dupes in place on the result object
            Iterator<String> pointerIterator = result.getPointers().iterator();
            while (pointerIterator.hasNext()) {
                String pointer = pointerIterator.next();
                String normalisedKey = normaliser.buildNormalisedKey(pointer);

                if (!seen.add(normalisedKey)) {
                    // Already seen within this rule - remove just this pointer
                    pointerIterator.remove();
                }
            }

            // Only add to output if result still has pointers remaining
            if (!result.getPointers().isEmpty()) {
                output.add(result);
            }
        }

        return output;
    }
}
