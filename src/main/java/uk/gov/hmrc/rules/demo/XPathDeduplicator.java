package uk.gov.hmrc.rules.demo;

import java.util.*;

/**
 * Orchestrates deduplication of XPath pointers across a list of RuleFiredValidationResult objects.
 *
 * Delegates XPath normalisation to XPathNormaliser.
 * Delegates pointer selection to PointerSelectionStrategy.
 * Optionally produces a detailed report via DeduplicationReporter (development use only).
 *
 * Three pass algorithm:
 * - Pass 1: Collect all pointer candidates grouped by rule ID and normalised key
 * - Pass 2: For each group, use strategy to select winner, remove losers from parent results
 * - Pass 3: Build output list, excluding results with empty pointer lists
 *
 * @version 0.3.0-SNAPSHOT
 * @since 2026-02-27
 */
public class XPathDeduplicator {

    private final XPathNormaliser normaliser;
    private final PointerSelectionStrategy strategy;
    private final boolean reportingEnabled;
    private final DeduplicationReporter reporter;

    /**
     * Production constructor - reporting disabled.
     */
    public XPathDeduplicator(NormalisationConfig config, PointerSelectionStrategy strategy) {
        this(config, strategy, false);
    }

    /**
     * Development constructor - reporting optionally enabled.
     *
     * @param reportingEnabled when true produces a detailed deduplication report to the log.
     *                         FOR DEVELOPMENT USE ONLY - disable in production.
     */
    public XPathDeduplicator(NormalisationConfig config, PointerSelectionStrategy strategy, boolean reportingEnabled) {
        this.normaliser = new XPathNormaliser(config);
        this.strategy = strategy;
        this.reportingEnabled = reportingEnabled;
        this.reporter = reportingEnabled ? new DeduplicationReporter() : null;
    }

    /**
     * Deduplicates pointers across a list of RuleFiredValidationResult objects.
     *
     * @param results list of validation results to deduplicate
     * @return new list in original order, containing only results that still have pointers
     */
    public List<RuleFiredValidationResult> deduplicate(List<RuleFiredValidationResult> results) {
        int originalCount = results.size();

        // Pass 1 - collect candidates grouped by rule ID then normalised key
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

        // Pass 2 - for each group select winner, remove losers from parent results
        // Track winners for reporting
        Map<String, Map<String, PointerCandidate>> winnersByRuleAndKey =
                reportingEnabled ? new LinkedHashMap<>() : null;

        for (Map.Entry<String, Map<String, List<PointerCandidate>>> ruleEntry
                : candidatesByRuleAndKey.entrySet()) {

            String ruleId = ruleEntry.getKey();

            for (Map.Entry<String, List<PointerCandidate>> keyEntry : ruleEntry.getValue().entrySet()) {
                String normalisedKey = keyEntry.getKey();
                List<PointerCandidate> candidates = keyEntry.getValue();

                if (candidates.size() > 1) {
                    PointerCandidate winner = strategy.select(candidates);

                    if (reportingEnabled) {
                        winnersByRuleAndKey
                                .computeIfAbsent(ruleId, k -> new LinkedHashMap<>())
                                .put(normalisedKey, winner);
                    }

                    for (PointerCandidate candidate : candidates) {
                        if (candidate != winner) {
                            candidate.parent.getPointers().remove(candidate.pointer);
                        }
                    }
                }
            }
        }

        // Pass 3 - build output list preserving original order, excluding empty results
        List<RuleFiredValidationResult> output = new ArrayList<>();
        List<RuleFiredValidationResult> removedResults =
                reportingEnabled ? new ArrayList<>() : null;

        for (RuleFiredValidationResult result : results) {
            if (!result.getPointers().isEmpty()) {
                output.add(result);
            } else if (reportingEnabled) {
                removedResults.add(result);
            }
        }

        // Report if enabled
        if (reportingEnabled) {
            reporter.report(candidatesByRuleAndKey, winnersByRuleAndKey,
                    removedResults, originalCount, output.size());
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
