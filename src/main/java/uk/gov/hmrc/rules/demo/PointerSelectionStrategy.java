package uk.gov.hmrc.rules.demo;

import java.util.List;

/**
 * Strategy interface for selecting which pointer to keep when duplicates are found.
 *
 * Implementations determine which candidate survives deduplication within a rule group.
 * The strategy receives all candidates that share the same normalised key within the
 * same rule, and returns the raw pointer string that should be kept.
 *
 * Current implementations:
 * - FirstOccurrenceStrategy  - keeps the first pointer encountered (release 2)
 * - LowestSequenceNumberStrategy - keeps the pointer with lowest sequence number (release 3)
 *
 * @version 0.2.0-SNAPSHOT
 * @since 2026-03-02
 */
@FunctionalInterface
public interface PointerSelectionStrategy {

    /**
     * Selects the pointer to keep from a list of duplicate candidates.
     *
     * @param candidates all pointer candidates sharing the same normalised key within a rule
     * @return the raw pointer string that should be retained
     */
    String select(List<XPathDeduplicator.PointerCandidate> candidates);
}
