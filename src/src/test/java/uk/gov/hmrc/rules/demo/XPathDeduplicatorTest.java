package uk.gov.hmrc.rules.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class XPathDeduplicatorTest {

    private XPathDeduplicator deduplicator;

    @BeforeEach
    void setUp() {
        deduplicator = new XPathDeduplicator(Map.of(
                "partyRoleType",         false,  // value matters - don't normalise
                "countryRegionRoleType", true    // value doesn't matter - normalise
        ));
    }

    @Test
    void shouldDeduplicatePartiesWithSameRoleTypeButDifferentSequenceNumbers() {
        List<String> input = List.of(
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification",
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]/partyIdentification"
        );

        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void shouldNotDeduplicatePartiesWithDifferentRoleTypes() {
        List<String> input = List.of(
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification",
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"CN\" and sequenceNumber=\"1\"]/partyIdentification"
        );

        assertThat(deduplicator.deduplicate(input)).containsExactlyElementsOf(input);
    }

    @Test
    void shouldDeduplicateSpecialProceduresWithDifferentSequenceNumbers() {
        List<String> input = List.of(
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/procedureCombination/specialProcedures[sequenceNumber=\"1\"]",
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/procedureCombination/specialProcedures[sequenceNumber=\"2\"]"
        );

        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void shouldDeduplicateCountryRegionRolesRegardlessOfRoleTypeValue() {
        List<String> input = List.of(
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"1\"]/countryCode",
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/countryRegionRoles[countryRegionRoleType=\"5\" and sequenceNumber=\"1\"]/countryCode"
        );

        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void shouldNotDeduplicateSamePathAcrossDifferentGoodsItems() {
        List<String> input = List.of(
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/procedureCombination/specialProcedures[sequenceNumber=\"1\"]",
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"2\"]/procedureCombination/specialProcedures[sequenceNumber=\"1\"]"
        );

        assertThat(deduplicator.deduplicate(input)).containsExactlyElementsOf(input);
    }

    @Test
    void shouldAlwaysKeepFirstOccurrenceNotNormalisedVersion() {
        String first  = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification";
        String second = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]/partyIdentification";

        List<String> result = deduplicator.deduplicate(List.of(first, second));

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(first);
    }

    @Test
    void shouldHandlePathsWithNoPredicatesInTail() {
        List<String> input = List.of(
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/invoiceValue",
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/invoiceValue"
        );

        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void shouldHandleMixedPathsInOneList() {
        List<String> input = List.of(
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification",
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]/partyIdentification",
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"2\"]/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification",
                "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"CN\" and sequenceNumber=\"1\"]/partyIdentification"
        );

        List<String> result = deduplicator.deduplicate(input);

        assertThat(result).containsExactly(
                input.get(0), // goodsItem 1, LC - kept
                input.get(2), // goodsItem 2, LC - different goods item so kept
                input.get(3)  // goodsItem 1, CN - different role type so kept
                // input.get(1) dropped - duplicate of input.get(0)
        );
    }
}
