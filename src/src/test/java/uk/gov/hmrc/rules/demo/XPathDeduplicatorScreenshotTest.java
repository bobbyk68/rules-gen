package uk.gov.hmrc.rules.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class XPathDeduplicatorScreenshotTest {

    private static final String BASE = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]";

    private XPathDeduplicator deduplicator;

    @BeforeEach
    void setUp() {
        deduplicator = new XPathDeduplicator(Map.of(
                "partyRoleType",         false,  // value matters - don't normalise
                "countryRegionRoleType", true    // value doesn't matter - normalise
        ));
    }

    // -------------------------------------------------------------------------
    // Screenshot 1 - Jira comments
    // -------------------------------------------------------------------------

    @Test
    void screenshot1_partiesLcPartyIdentification_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification",
                BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]/partyIdentification"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    // -------------------------------------------------------------------------
    // Screenshot 2 - IDE search results
    // -------------------------------------------------------------------------

    @Test
    void screenshot2_declaredCustomsValue_duplicatesShouldDedupe() {
        List<String> input = List.of(
                BASE + "/declaredCustomsValue",
                BASE + "/declaredCustomsValue"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_declaredDutyTaxFees_arrayIndicator_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/declaredDutyTaxFees[]",
                BASE + "/declaredDutyTaxFees[]"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_declaredDutyTaxFees_payableAmount_measureUnitType_shouldDedupe() {
        // TODO: right side of path cut off in screenshot - verify measureUnitType is correct terminal field
        List<String> input = List.of(
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/payableAmount/measureUnitType",
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/payableAmount/measureUnitType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_declaredDutyTaxFees_preferenceType_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType",
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/preferenceType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_declaredDutyTaxFees_quotaOrderNumber_duplicates_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/quotaOrderNumber",
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/quotaOrderNumber"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_declaredDutyTaxFees_rate_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/rate",
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/rate"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_declaredDutyTaxFees_specificBase_measureUnitType_shouldDedupe() {
        // TODO: right side partially cut off in screenshot - verify terminal field name
        List<String> input = List.of(
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/specificBase/measureUnitType",
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/specificBase/measureUnitType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_declaredDutyTaxFees_totalAmount_measureUnitType_shouldDedupe() {
        // TODO: right side partially cut off in screenshot - verify terminal field name
        List<String> input = List.of(
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/totalAmount/measureUnitType",
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/totalAmount/measureUnitType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_invoiceValue_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/invoiceValue",
                BASE + "/invoiceValue"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_invoiceValue_measureUnitType_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/invoiceValue/measureUnitType",
                BASE + "/invoiceValue/measureUnitType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_packaging_arrayIndicator_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/packaging[]",
                BASE + "/packaging[]"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_packaging_marksNumbers_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/packaging[sequenceNumber=\"1\"]/marksNumbers",
                BASE + "/packaging[sequenceNumber=\"2\"]/marksNumbers"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_packaging_quantity_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/packaging[sequenceNumber=\"1\"]/quantity",
                BASE + "/packaging[sequenceNumber=\"2\"]/quantity"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot2_packaging_packageType_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/packaging[sequenceNumber=\"1\"]/packageType",
                BASE + "/packaging[sequenceNumber=\"1\"]/packageType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    // -------------------------------------------------------------------------
    // Screenshot 3 - More paths
    // -------------------------------------------------------------------------

    @Test
    void screenshot3_valuationAdjustments_amount_shouldDedupe() {
        // TODO: partially cut off at top of screenshot - verify full path
        List<String> input = List.of(
                BASE + "/valuationAdjustments[sequenceNumber=\"1\"]/amount",
                BASE + "/valuationAdjustments[sequenceNumber=\"2\"]/amount"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_additionalInformation_arrayIndicator_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/additionalInformation[]",
                BASE + "/additionalInformation[]"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_partiesEx_physicalAddress_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"EX\"]/physicalAddress",
                BASE + "/parties[partyRoleType=\"EX\"]/physicalAddress"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_partiesLc_partySubRoleType_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partySubRoleType",
                BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]/partySubRoleType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_partiesBy_arrayIndicator_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"BY\"]",
                BASE + "/parties[partyRoleType=\"BY\"]"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_partiesLc_withSequenceNumber_shouldDedupe() {
        // TODO: terminal field cut off in screenshot - verify
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]",
                BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_countryRegionRoles_roleType5_countryCode_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/countryRegionRoles[countryRegionRoleType=\"5\" and sequenceNumber=\"1\"]/countryCode",
                BASE + "/countryRegionRoles[countryRegionRoleType=\"5\" and sequenceNumber=\"2\"]/countryCode"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_countryRegionRoles_roleType3_countryRegionSubRoleType_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"1\"]/countryRegionSubRoleType",
                BASE + "/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"2\"]/countryRegionSubRoleType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_countryRegionRoles_differentRoleTypes_shouldDedupeAsCountryRegionRoleTypeIsNormalised() {
        // countryRegionRoleType configured true so role type 3 vs 5 still dedupes
        List<String> input = List.of(
                BASE + "/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"1\"]/countryCode",
                BASE + "/countryRegionRoles[countryRegionRoleType=\"5\" and sequenceNumber=\"1\"]/countryCode"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_commodity_netMass_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/commodity/netMass",
                BASE + "/commodity/netMass"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_partiesBy_physicalAddress_streetAndNumber_shouldDedupe() {
        // TODO: streetAndNumber partially obscured - verify field name
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/streetAndNumber",
                BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/streetAndNumber"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_partiesBy_physicalAddress_cityName_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/cityName",
                BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/cityName"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_partiesBy_physicalAddress_zipCode_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/zipCode",
                BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/zipCode"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_procedureCombination_specialProcedures_differentSeqNumbers_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/procedureCombination/specialProcedures[sequenceNumber=\"1\"]",
                BASE + "/procedureCombination/specialProcedures[sequenceNumber=\"2\"]"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_additionalDocuments_type_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/additionalDocuments[sequenceNumber=\"1\"]/type",
                BASE + "/additionalDocuments[sequenceNumber=\"2\"]/type"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_additionalInformation_code_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/additionalInformation[sequenceNumber=\"1\"]/code",
                BASE + "/additionalInformation[sequenceNumber=\"2\"]/code"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_invoiceValue_measureUnitType_duplicates_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/invoiceValue/measureUnitType",
                BASE + "/invoiceValue/measureUnitType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_declaredCustomsValue_measureUnitType_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/declaredCustomsValue/measureUnitType",
                BASE + "/declaredCustomsValue/measureUnitType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_countryRegionRoles_roleType3_countryCode_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"1\"]/countryCode",
                BASE + "/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"2\"]/countryCode"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_declaredDutyTaxFees_quotaOrderNumber_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/quotaOrderNumber",
                BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/quotaOrderNumber"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_preferenceType_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/preferenceType",
                BASE + "/preferenceType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_partiesCn_partyName_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"CN\"]/partyName",
                BASE + "/parties[partyRoleType=\"CN\"]/partyName"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_partiesCn_partyIdentification_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"CN\"]/partyIdentification",
                BASE + "/parties[partyRoleType=\"CN\"]/partyIdentification"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_partiesCn_physicalAddress_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"CN\"]/physicalAddress",
                BASE + "/parties[partyRoleType=\"CN\"]/physicalAddress"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_procedureCombination_previousProcedureType_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/procedureCombination/previousProcedureType",
                BASE + "/procedureCombination/previousProcedureType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_packaging_quantity_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/packaging[sequenceNumber=\"1\"]/quantity",
                BASE + "/packaging[sequenceNumber=\"2\"]/quantity"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    @Test
    void screenshot3_valuationIndicatorType_shouldDedupe() {
        List<String> input = List.of(
                BASE + "/valuationIndicatorType",
                BASE + "/valuationIndicatorType"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactly(input.get(0));
    }

    // -------------------------------------------------------------------------
    // Cross-cutting: different role types should NOT dedupe
    // -------------------------------------------------------------------------

    @Test
    void partiesLcVsCn_shouldNotDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification",
                BASE + "/parties[partyRoleType=\"CN\" and sequenceNumber=\"1\"]/partyIdentification"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactlyElementsOf(input);
    }

    @Test
    void partiesLcVsByVsEx_allDifferentRoleTypes_shouldNotDedupe() {
        List<String> input = List.of(
                BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification",
                BASE + "/parties[partyRoleType=\"BY\" and sequenceNumber=\"1\"]/partyIdentification",
                BASE + "/parties[partyRoleType=\"EX\" and sequenceNumber=\"1\"]/partyIdentification"
        );
        assertThat(deduplicator.deduplicate(input)).containsExactlyElementsOf(input);
    }

    // -------------------------------------------------------------------------
    // Cross-cutting: different goodsItems sequence numbers should NOT dedupe
    // -------------------------------------------------------------------------

    @Test
    void differentGoodsItems_sameField_shouldNotDedupe() {
        String goodsItem1 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/invoiceValue";
        String goodsItem2 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"2\"]/invoiceValue";
        assertThat(deduplicator.deduplicate(List.of(goodsItem1, goodsItem2)))
                .containsExactly(goodsItem1, goodsItem2);
    }

    @Test
    void differentGoodsItems_specialProcedures_shouldNotDedupe() {
        String goodsItem1 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]/procedureCombination/specialProcedures[sequenceNumber=\"1\"]";
        String goodsItem2 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"2\"]/procedureCombination/specialProcedures[sequenceNumber=\"1\"]";
        assertThat(deduplicator.deduplicate(List.of(goodsItem1, goodsItem2)))
                .containsExactly(goodsItem1, goodsItem2);
    }
}
