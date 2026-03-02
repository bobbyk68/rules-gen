package uk.gov.hmrc.rules.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for XPathNormaliser - key building and sequence number extraction.
 *
 * @version 0.2.0-SNAPSHOT
 * @since 2026-03-02
 */
class XPathNormaliserTest {

    private XPathNormaliser normaliser;

    @BeforeEach
    void setUp() {
        NormalisationConfig config = new NormalisationConfig(Map.of(
                "partyRoleType",         false,
                "countryRegionRoleType", true
        ));
        normaliser = new XPathNormaliser(config);
    }

    // -------------------------------------------------------------------------
    // buildNormalisedKey
    // -------------------------------------------------------------------------

    @Test
    void shouldStripSequenceNumberFromTail() {
        String xpath = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/packaging[sequenceNumber=\"2\"]/packageType";
        String key   = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/packaging[sequenceNumber]/packageType";

        assertThat(normaliser.buildNormalisedKey(xpath)).isEqualTo(key);
    }

    @Test
    void shouldPreserveGoodsItemSequenceNumber() {
        String xpath1 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/invoiceValue";
        String xpath2 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"2\"]/invoiceValue";

        assertThat(normaliser.buildNormalisedKey(xpath1))
                .isNotEqualTo(normaliser.buildNormalisedKey(xpath2));
    }

    @Test
    void shouldKeepPartyRoleTypeValue() {
        String lcXpath = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification";
        String cnXpath = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"CN\" and sequenceNumber=\"1\"]/partyIdentification";

        assertThat(normaliser.buildNormalisedKey(lcXpath))
                .isNotEqualTo(normaliser.buildNormalisedKey(cnXpath));
    }

    @Test
    void shouldNormaliseCountryRegionRoleTypeValue() {
        String xpath3 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"1\"]/countryCode";
        String xpath5 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/countryRegionRoles[countryRegionRoleType=\"5\" and sequenceNumber=\"1\"]/countryCode";

        assertThat(normaliser.buildNormalisedKey(xpath3))
                .isEqualTo(normaliser.buildNormalisedKey(xpath5));
    }

    // -------------------------------------------------------------------------
    // extractSequenceNumbers
    // -------------------------------------------------------------------------

    @Test
    void shouldExtractSingleSequenceNumberFromTail() {
        String xpath = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/packaging[sequenceNumber=\"3\"]/packageType";

        assertThat(normaliser.extractSequenceNumbers(xpath)).containsExactly(3);
    }

    @Test
    void shouldExtractMultipleSequenceNumbersFromTail() {
        String xpath = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/declaredDutyTaxFees[sequenceNumber=\"2\"]/address[sequenceNumber=\"1\"]";

        assertThat(normaliser.extractSequenceNumbers(xpath)).containsExactly(2, 1);
    }

    @Test
    void shouldNotExtractGoodsItemSequenceNumber() {
        String xpath = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/invoiceValue";

        assertThat(normaliser.extractSequenceNumbers(xpath)).isEmpty();
    }

    @Test
    void shouldExtractSequenceNumberFromCompoundPredicate() {
        String xpath = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/parties[partyRoleType=\"LC\" and sequenceNumber=\"3\"]/partyIdentification";

        assertThat(normaliser.extractSequenceNumbers(xpath)).containsExactly(3);
    }
}
