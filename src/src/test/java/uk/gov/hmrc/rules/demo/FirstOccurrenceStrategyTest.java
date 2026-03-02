package uk.gov.hmrc.rules.demo;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for FirstOccurrenceStrategy.
 *
 * @version 0.2.0-SNAPSHOT
 * @since 2026-03-02
 */
class FirstOccurrenceStrategyTest {

    private final FirstOccurrenceStrategy strategy = new FirstOccurrenceStrategy();

    @Test
    void shouldReturnFirstCandidatePointer() {
        List<XPathDeduplicator.PointerCandidate> candidates = List.of(
                candidate("/pointer/sequenceNumber=\"3\"", List.of(3)),
                candidate("/pointer/sequenceNumber=\"1\"", List.of(1)),
                candidate("/pointer/sequenceNumber=\"2\"", List.of(2))
        );

        assertThat(strategy.select(candidates)).isEqualTo("/pointer/sequenceNumber=\"3\"");
    }

    @Test
    void shouldReturnOnlyCandidate() {
        List<XPathDeduplicator.PointerCandidate> candidates = List.of(
                candidate("/pointer/sequenceNumber=\"1\"", List.of(1))
        );

        assertThat(strategy.select(candidates)).isEqualTo("/pointer/sequenceNumber=\"1\"");
    }

    private XPathDeduplicator.PointerCandidate candidate(String pointer, List<Integer> sequenceNumbers) {
        return new XPathDeduplicator.PointerCandidate(pointer, null, sequenceNumbers);
    }
}
