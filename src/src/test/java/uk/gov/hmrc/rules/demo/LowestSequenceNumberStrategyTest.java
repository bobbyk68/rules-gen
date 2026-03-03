package uk.gov.hmrc.cars.rules.dms.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for LowestSequenceNumberStrategy.
 *
 * Covers:
 * - Single sequence number - lowest wins
 * - Composite sequence numbers - first difference determines winner
 * - Empty sequence numbers - first occurrence wins (stable sort tiebreaker)
 * - Mixed empty and non-empty - non-empty treated as deeper in tree
 * - Integration with XPathDeduplicator - full end to end with real results
 *
 * @version 0.3.0-SNAPSHOT
 * @since 2026-03-02
 */
class LowestSequenceNumberStrategyTest {

    private static final String BASE_1 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]";

    private LowestSequenceNumberStrategy strategy;
    private XPathDeduplicator deduplicator;

    @BeforeEach
    void setUp() {
        strategy = new LowestSequenceNumberStrategy();

        NormalisationConfig config = new NormalisationConfig(Map.of(
                "partyRoleType",         false,
                "countryRegionRoleType", true
        ));
        deduplicator = new XPathDeduplicator(config, strategy);
    }

    // -------------------------------------------------------------------------
    // Strategy unit tests - select() in isolation
    // -------------------------------------------------------------------------

    @Test
    void singleSequenceNumber_lowestWins() {
        List<XPathDeduplicator.PointerCandidate> candidates = List.of(
                candidate("pointer-seq3", List.of(3)),
                candidate("pointer-seq1", List.of(1)),
                candidate("pointer-seq2", List.of(2))
        );

        assertThat(strategy.select(candidates).pointer).isEqualTo("pointer-seq1");
    }

    @Test
    void compositeSequenceNumbers_firstDifferenceDecides() {
        // [2,1] vs [1,3] - first element 2 vs 1, B wins immediately
        List<XPathDeduplicator.PointerCandidate> candidates = List.of(
                candidate("pointer-a", List.of(2, 1)),
                candidate("pointer-b", List.of(1, 3))
        );

        assertThat(strategy.select(candidates).pointer).isEqualTo("pointer-b");
    }

    @Test
    void compositeSequenceNumbers_secondElementDecides() {
        // [1,3] vs [1,1] - first equal, second 3 vs 1, B wins
        List<XPathDeduplicator.PointerCandidate> candidates = List.of(
                candidate("pointer-a", List.of(1, 3)),
                candidate("pointer-b", List.of(1, 1))
        );

        assertThat(strategy.select(candidates).pointer).isEqualTo("pointer-b");
    }

    @Test
    void shorterListWins_whenAllComparedElementsEqual() {
        // [1] vs [1,2] - first equal, A exhausted - A wins (higher in tree)
        List<XPathDeduplicator.PointerCandidate> candidates = List.of(
                candidate("pointer-a", List.of(1)),
                candidate("pointer-b", List.of(1, 2))
        );

        assertThat(strategy.select(candidates).pointer).isEqualTo("pointer-a");
    }

    @Test
    void emptySequenceNumbers_firstOccurrenceWins() {
        // Both empty - stable sort preserves original order
        List<XPathDeduplicator.PointerCandidate> candidates = new ArrayList<>(List.of(
                candidate("pointer-first",  List.of()),
                candidate("pointer-second", List.of())
        ));

        assertThat(strategy.select(candidates).pointer).isEqualTo("pointer-first");
    }

    @Test
    void singleCandidate_returnedDirectly() {
        List<XPathDeduplicator.PointerCandidate> candidates = List.of(
                candidate("pointer-only", List.of(1))
        );

        assertThat(strategy.select(candidates).pointer).isEqualTo("pointer-only");
    }

    // -------------------------------------------------------------------------
    // Integration tests - full dedup with LowestSequenceNumberStrategy
    // -------------------------------------------------------------------------

    @Test
    void integration_lowestSequenceNumberKept_notFirstOccurrence() {
        // pos 1 has seq 3, pos 2 has seq 1, pos 3 has seq 2
        // pos 2 should survive even though it is not first occurrence
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", BASE_1 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"3\"]/partyIdentification"),
                result("BR255_666", BASE_1 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification"),
                result("BR255_666", BASE_1 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]/partyIdentification")
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(
                BASE_1 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification"
        );
    }

    @Test
    void integration_identicalPointers_firstOccurrenceKept() {
        // All 3 have identical pointer strings and empty sequence numbers
        // First occurrence should survive
        String pointer = BASE_1 + "/procedureCombination/previousProcedureType";

        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR459_400", pointer),
                result("BR459_400", pointer),
                result("BR459_400", pointer)
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer);
    }

    @Test
    void integration_differentRules_independentLowestSequence() {
        // Each rule independently picks its own lowest sequence number
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", BASE_1 + "/packaging[sequenceNumber=\"3\"]/packageType"),
                result("BR455_202", BASE_1 + "/additionalInformation[sequenceNumber=\"2\"]/code"),
                result("BR255_666", BASE_1 + "/packaging[sequenceNumber=\"1\"]/packageType"),
                result("BR455_202", BASE_1 + "/additionalInformation[sequenceNumber=\"1\"]/code")
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(2);
        assertThat(output.get(0).getPointers()).containsExactly(
                BASE_1 + "/packaging[sequenceNumber=\"1\"]/packageType"
        );
        assertThat(output.get(1).getPointers()).containsExactly(
                BASE_1 + "/additionalInformation[sequenceNumber=\"1\"]/code"
        );
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private XPathDeduplicator.PointerCandidate candidate(String pointer, List<Integer> sequenceNumbers) {
        return new XPathDeduplicator.PointerCandidate(pointer, null, sequenceNumbers);
    }

    private RuleFiredValidationResult result(String ruleId, String pointer) {
        ValidationResultContext context = ValidationResultContext.of()
                .ruleId(ruleId)
                .errorCode("DMS10002")
                .errorMessage("Obligation Error: Data Element is not allowed")
                .pointers(new ArrayList<>(List.of(pointer)))
                .goodsItemSequenceNumber(1)
                .build();

        return (RuleFiredValidationResult) ValidationResultBuilder.build(context);
    }
}
