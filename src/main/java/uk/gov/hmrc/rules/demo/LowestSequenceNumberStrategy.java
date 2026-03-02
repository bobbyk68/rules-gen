package uk.gov.hmrc.rules.demo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Strategy that keeps the pointer with the lowest sequence number in the tail path.
 *
 * This ensures we retain the first error you would encounter traversing the XML tree,
 * regardless of the order in which the rule engine fired the validation results.
 *
 * Sorting is done by composite comparison of extracted sequence numbers:
 * - Compare element by element from left to right
 * - First difference determines the winner - lower value wins
 * - If one list is exhausted before a difference is found - shorter list wins
 * - If both lists are empty or fully equal - original order preserved (stable sort)
 *   meaning first occurrence wins as a safe tiebreaker
 *
 * @version 0.3.0-SNAPSHOT
 * @since 2026-03-02
 */
public class LowestSequenceNumberStrategy implements PointerSelectionStrategy {

    @Override
    public XPathDeduplicator.PointerCandidate select(List<XPathDeduplicator.PointerCandidate> candidates) {
        // Copy the list before sorting - do not mutate the original candidate list
        List<XPathDeduplicator.PointerCandidate> sorted = new ArrayList<>(candidates);
        sorted.sort(byLowestSequenceNumber());
        return sorted.get(0);
    }

    /**
     * Builds a comparator that sorts PointerCandidates by their sequence numbers.
     * Compares element by element - first difference determines order.
     * Shorter list wins when all compared elements are equal (higher up the tree).
     * Empty lists compare as equal - stable sort preserves first occurrence.
     */
    private Comparator<XPathDeduplicator.PointerCandidate> byLowestSequenceNumber() {
        return (a, b) -> {
            List<Integer> aSeq = a.sequenceNumbers;
            List<Integer> bSeq = b.sequenceNumbers;

            int compareLength = Math.min(aSeq.size(), bSeq.size());

            // Walk both lists element by element up to the shorter length
            for (int i = 0; i < compareLength; i++) {
                int comparison = Integer.compare(aSeq.get(i), bSeq.get(i));
                if (comparison != 0) {
                    // Found a difference - lower value wins
                    return comparison;
                }
            }

            // All compared elements were equal - shorter list wins
            // If both empty this returns 0 and stable sort preserves original order
            return Integer.compare(aSeq.size(), bSeq.size());
        };
    }
}
