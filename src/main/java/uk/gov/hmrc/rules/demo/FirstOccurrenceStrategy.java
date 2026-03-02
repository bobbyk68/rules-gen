package uk.gov.hmrc.rules.demo;

import java.util.List;

/**
 * Strategy that keeps the first pointer encountered in the list.
 *
 * This is the simplest strategy - single pass, first occurrence wins.
 * Produces identical behaviour to release 0.1.0-SNAPSHOT.
 *
 * Use this strategy when the order of rule firing is guaranteed to be
 * meaningful, or when the client has not specified a preference.
 *
 * @version 0.2.0-SNAPSHOT
 * @since 2026-03-02
 */
public class FirstOccurrenceStrategy implements PointerSelectionStrategy {

    @Override
    public String select(List<XPathDeduplicator.PointerCandidate> candidates) {
        return candidates.get(0).pointer;
    }
}
