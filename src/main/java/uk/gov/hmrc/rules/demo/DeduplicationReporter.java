package uk.gov.hmrc.rules.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Produces a human-readable report of what happened during deduplication.
 *
 * FOR DEVELOPMENT USE ONLY - not for production.
 * Enabled via the reportingEnabled flag on XPathDeduplicator constructor.
 *
 * Report includes:
 * - Per rule, per normalised key: which pointer was kept and which were removed
 * - Which RuleFiredValidationResult objects were excluded entirely
 * - Summary counts: original vs deduped result count, pointers removed
 *
 * Useful for validating correctness against large real-world payloads where
 * visual inspection of raw results is not practical.
 *
 * @version 0.3.0-SNAPSHOT
 * @since 2026-03-02
 */
class DeduplicationReporter {

    private static final Logger log = LoggerFactory.getLogger(DeduplicationReporter.class);
    private static final String DIVIDER = "=".repeat(80);
    private static final String THIN_DIVIDER = "-".repeat(60);

    /**
     * Logs the full deduplication report after all passes are complete.
     *
     * @param candidatesByRuleAndKey  map built in pass 1 - all candidates grouped by rule and normalised key
     * @param winnersByRuleAndKey     map built in pass 2 - winning candidate per rule per normalised key
     * @param removedResults          results excluded entirely in pass 3 - all pointers were deduped away
     * @param originalCount           total result count before dedup
     * @param dedupedCount            total result count after dedup
     */
    void report(
            Map<String, Map<String, List<XPathDeduplicator.PointerCandidate>>> candidatesByRuleAndKey,
            Map<String, Map<String, XPathDeduplicator.PointerCandidate>> winnersByRuleAndKey,
            List<RuleFiredValidationResult> removedResults,
            int originalCount,
            int dedupedCount
    ) {
        log.info("\n{}", DIVIDER);
        log.info("=== Deduplication Report ===");
        log.info("{}", DIVIDER);

        int totalPointersRemoved = 0;

        for (Map.Entry<String, Map<String, List<XPathDeduplicator.PointerCandidate>>> ruleEntry
                : candidatesByRuleAndKey.entrySet()) {

            String ruleId = ruleEntry.getKey();
            Map<String, List<XPathDeduplicator.PointerCandidate>> byKey = ruleEntry.getValue();

            // Only report rules where at least one group had duplicates
            boolean ruleHasDupes = byKey.values().stream().anyMatch(c -> c.size() > 1);
            if (!ruleHasDupes) {
                continue;
            }

            log.info("\nRule: {}", ruleId);
            log.info("{}", THIN_DIVIDER);

            for (Map.Entry<String, List<XPathDeduplicator.PointerCandidate>> keyEntry : byKey.entrySet()) {
                String normalisedKey = keyEntry.getKey();
                List<XPathDeduplicator.PointerCandidate> candidates = keyEntry.getValue();

                if (candidates.size() <= 1) {
                    continue; // no dedup happened for this key
                }

                XPathDeduplicator.PointerCandidate winner =
                        winnersByRuleAndKey.get(ruleId).get(normalisedKey);

                log.info("  Normalised key: {}", normalisedKey);

                for (XPathDeduplicator.PointerCandidate candidate : candidates) {
                    if (candidate == winner) {
                        log.info("    KEPT:    {}", candidate.pointer);
                    } else {
                        log.info("    REMOVED: {}", candidate.pointer);
                        totalPointersRemoved++;
                    }
                }
            }
        }

        // Results removed entirely in pass 3
        if (!removedResults.isEmpty()) {
            log.info("\n{}", DIVIDER);
            log.info("=== Results removed entirely (all pointers deduped away) ===");
            log.info("{}", THIN_DIVIDER);
            for (RuleFiredValidationResult removed : removedResults) {
                log.info("  Rule: {} - goodsItemSequenceNumber={}",
                        removed.getRule().getId(),
                        removed.getGoodsItemSequenceNumber());
            }
        }

        // Summary
        log.info("\n{}", DIVIDER);
        log.info("=== Summary ===");
        log.info("{}", THIN_DIVIDER);
        log.info("  Original result count : {}", originalCount);
        log.info("  Deduped result count  : {}", dedupedCount);
        log.info("  Results removed       : {}", originalCount - dedupedCount);
        log.info("  Pointers removed      : {}", totalPointersRemoved);
        log.info("{}\n", DIVIDER);
    }
}
