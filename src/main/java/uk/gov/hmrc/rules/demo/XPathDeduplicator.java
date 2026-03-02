package uk.gov.hmrc.rules.demo;

import java.util.*;

/**
 * Orchestrates deduplication of XPath pointers across a list of RuleFiredValidationResult objects.
 *
 * Delegates XPath normalisation to XPathNormaliser.
 * Delegates pointer selection to PointerSelectionStrategy.
 *
 * Rules:
 * - Order of results is preserved throughout
 * - Dedup is scoped per rule ID - pointers only compared within the same rule
 * - Which pointer survives is determined by the injected PointerSelectionStrategy
 * - Duplicate pointers are removed from the mutable pointers list on each result
 * - If a result ends up with no pointers it is excluded from the output
 * - The input list is never modified - a new output list is built
 *
 * @version 0.2.0-SNAPSHOT
 * @since 2026-02-27
 */
public class XPathDeduplicator {

    private final XPathNormaliser normaliser;
    private final PointerSelectionStrategy strategy;

    public XPathDeduplicator(NormalisationConfig config, PointerSelectionStrategy strategy) {
        this.normaliser = new XPathNormaliser(config);
        this.strategy = strategy;
    }

    /**
     * Deduplicates pointers across a list of RuleFiredValidationResult objects.
     *
     * Pass 1: Collect all pointer candidates grouped by rule ID and normalised key
     * Pass 2: For each group, use strategy to select winner, remove losers from parent results
     * Pass 3: Build output list, excluding results with empty pointer lists
     *
     * @param results list of validation results to deduplicate
     * @return new list in original order, containing only results that still have pointers
     */
    public List<RuleFiredValidationResult> deduplicate(List<RuleFiredValidationResult> results) {
        // Pass 1 - collect candidates
        Map<String, Map<String, List<PointerCandidate>>> candidatesByRuleAndKey = new LinkedHashMap<>();

        for (RuleFiredValidationResult result : results) {
            String ruleId = result.getRule().getId();

            for (String pointer : result.getPointers()) {
                String normalisedKey = normaliser.buildNormalisedKey(pointer);
                List<Integer> sequenceNumbers = normaliser.extractSequenceNumbers(pointer);

                PointerCandidate candidate = new PointerCandidate(pointer, result, sequenceNumbers);

                candidatesByRuleAndKey
                        .computeIfAbsent(ruleId, k -> new LinkedHashMap<>())
                        .computeIfAbsent(normalisedKey, k -> new ArrayList<>())
                        .add(candidate);
            }
        }

        // Pass 2 - for each group, select winner, remove losers from parent results
        for (Map<String, List<PointerCandidate>> byKey : candidatesByRuleAndKey.values()) {
            for (List<PointerCandidate> candidates : byKey.values()) {
                if (candidates.size() > 1) {
                    String winningPointer = strategy.select(candidates);
                    for (PointerCandidate candidate : candidates) {
                        if (!candidate.pointer.equals(winningPointer)) {
                            candidate.parent.getPointers().remove(candidate.pointer);
                        }
                    }
                }
            }
        }

        // Pass 3 - build output list preserving original order, excluding empty results
        List<RuleFiredValidationResult> output = new ArrayList<>();
        for (RuleFiredValidationResult result : results) {
            if (!result.getPointers().isEmpty()) {
                output.add(result);
            }
        }

        return output;
    }

    /**
     * Temporary data holder used during the dedup algorithm.
     * Links a raw pointer string to its parent result and extracted sequence numbers.
     * Private to XPathDeduplicator - not visible outside this class.
     */
    static class PointerCandidate {
        final String pointer;
        final RuleFiredValidationResult parent;
        final List<Integer> sequenceNumbers;

        PointerCandidate(String pointer, RuleFiredValidationResult parent, List<Integer> sequenceNumbers) {
            this.pointer = pointer;
            this.parent = parent;
            this.sequenceNumbers = sequenceNumbers;
        }
    }
}
