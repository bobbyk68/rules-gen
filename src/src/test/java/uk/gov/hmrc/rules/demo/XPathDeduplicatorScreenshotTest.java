package uk.gov.hmrc.cars.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmrc.cars.rules.dms.util.FirstOccurrenceStrategy;
import uk.gov.hmrc.cars.rules.dms.util.NormalisationConfig;
import uk.gov.hmrc.cars.rules.dms.util.XPathDeduplicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests derived from real XPath pointers observed in screenshots of CARS DMS validation responses.
 * Each test represents a real deduplication scenario from the system.
 *
 * All tests use FirstOccurrenceStrategy - first occurrence wins.
 *
 * @version 0.3.0-SNAPSHOT
 * @since 2026-02-27
 */
class XPathDeduplicatorScreenshotTest {

    private static final String BASE = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]";

    private XPathDeduplicator deduplicator;

    @BeforeEach
    void setUp() {
        NormalisationConfig config = new NormalisationConfig(Map.of(
                "partyRoleType",         false,
                "countryRegionRoleType", true
        ));
        deduplicator = new XPathDeduplicator(config, new FirstOccurrenceStrategy());
    }

    // -------------------------------------------------------------------------
    // Screenshot 1 - Jira comments
    // -------------------------------------------------------------------------

    @Test
    void screenshot1_partiesLcPartyIdentification_shouldDedupe() {
        String pointer1 = BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification";
        String pointer2 = BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]/partyIdentification";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    // -------------------------------------------------------------------------
    // Screenshot 2 - IDE search results
    // -------------------------------------------------------------------------

    @Test
    void screenshot2_declaredCustomsValue_duplicatesShouldDedupe() {
        String pointer1 = BASE + "/declaredCustomsValue";
        String pointer2 = BASE + "/declaredCustomsValue";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_declaredDutyTaxFees_arrayIndicator_shouldDedupe() {
        String pointer1 = BASE + "/declaredDutyTaxFees[]";
        String pointer2 = BASE + "/declaredDutyTaxFees[]";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_declaredDutyTaxFees_payableAmount_measureUnitType_shouldDedupe() {
        // TODO: right side of path cut off in screenshot - verify measureUnitType is correct terminal field
        String pointer1 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/payableAmount/measureUnitType";
        String pointer2 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/payableAmount/measureUnitType";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_declaredDutyTaxFees_preferenceType_shouldDedupe() {
        String pointer1 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType";
        String pointer2 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/preferenceType";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_declaredDutyTaxFees_quotaOrderNumber_duplicates_shouldDedupe() {
        String pointer1 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/quotaOrderNumber";
        String pointer2 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/quotaOrderNumber";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_declaredDutyTaxFees_rate_shouldDedupe() {
        String pointer1 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/rate";
        String pointer2 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/rate";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_declaredDutyTaxFees_specificBase_measureUnitType_shouldDedupe() {
        // TODO: right side partially cut off in screenshot - verify terminal field name
        String pointer1 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/specificBase/measureUnitType";
        String pointer2 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/specificBase/measureUnitType";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_declaredDutyTaxFees_totalAmount_measureUnitType_shouldDedupe() {
        // TODO: right side partially cut off in screenshot - verify terminal field name
        String pointer1 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/totalAmount/measureUnitType";
        String pointer2 = BASE + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/totalAmount/measureUnitType";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_invoiceValue_shouldDedupe() {
        String pointer1 = BASE + "/invoiceValue";
        String pointer2 = BASE + "/invoiceValue";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_invoiceValue_measureUnitType_shouldDedupe() {
        String pointer1 = BASE + "/invoiceValue/measureUnitType";
        String pointer2 = BASE + "/invoiceValue/measureUnitType";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_packaging_arrayIndicator_shouldDedupe() {
        String pointer1 = BASE + "/packaging[]";
        String pointer2 = BASE + "/packaging[]";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_packaging_marksNumbers_shouldDedupe() {
        String pointer1 = BASE + "/packaging[sequenceNumber=\"1\"]/marksNumbers";
        String pointer2 = BASE + "/packaging[sequenceNumber=\"2\"]/marksNumbers";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_packaging_quantity_shouldDedupe() {
        String pointer1 = BASE + "/packaging[sequenceNumber=\"1\"]/quantity";
        String pointer2 = BASE + "/packaging[sequenceNumber=\"2\"]/quantity";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot2_packaging_packageType_shouldDedupe() {
        String pointer1 = BASE + "/packaging[sequenceNumber=\"1\"]/packageType";
        String pointer2 = BASE + "/packaging[sequenceNumber=\"1\"]/packageType";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    // -------------------------------------------------------------------------
    // Screenshot 3 - More paths
    // -------------------------------------------------------------------------

    @Test
    void screenshot3_valuationAdjustments_amount_shouldDedupe() {
        // TODO: partially cut off at top of screenshot - verify full path
        String pointer1 = BASE + "/valuationAdjustments[sequenceNumber=\"1\"]/amount";
        String pointer2 = BASE + "/valuationAdjustments[sequenceNumber=\"2\"]/amount";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_additionalInformation_arrayIndicator_shouldDedupe() {
        String pointer1 = BASE + "/additionalInformation[]";
        String pointer2 = BASE + "/additionalInformation[]";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_partiesEx_physicalAddress_shouldDedupe() {
        String pointer1 = BASE + "/parties[partyRoleType=\"EX\"]/physicalAddress";
        String pointer2 = BASE + "/parties[partyRoleType=\"EX\"]/physicalAddress";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_partiesLc_partySubRoleType_shouldDedupe() {
        String pointer1 = BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partySubRoleType";
        String pointer2 = BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]/partySubRoleType";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_partiesBy_arrayIndicator_shouldDedupe() {
        String pointer1 = BASE + "/parties[partyRoleType=\"BY\"]";
        String pointer2 = BASE + "/parties[partyRoleType=\"BY\"]";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_partiesLc_withSequenceNumber_shouldDedupe() {
        // TODO: terminal field cut off in screenshot - verify
        String pointer1 = BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]";
        String pointer2 = BASE + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_countryRegionRoles_roleType5_countryCode_shouldDedupe() {
        String pointer1 = BASE + "/countryRegionRoles[countryRegionRoleType=\"5\" and sequenceNumber=\"1\"]/countryCode";
        String pointer2 = BASE + "/countryRegionRoles[countryRegionRoleType=\"5\" and sequenceNumber=\"2\"]/countryCode";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_countryRegionRoles_roleType3_countryRegionSubRoleType_shouldDedupe() {
        String pointer1 = BASE + "/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"1\"]/countryRegionSubRoleType";
        String pointer2 = BASE + "/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"2\"]/countryRegionSubRoleType";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_countryRegionRoles_differentRoleTypes_shouldDedupeAsCountryRegionRoleTypeIsNormalised() {
        // countryRegionRoleType configured true so role type 3 vs 5 still dedupes
        String pointer1 = BASE + "/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"1\"]/countryCode";
        String pointer2 = BASE + "/countryRegionRoles[countryRegionRoleType=\"5\" and sequenceNumber=\"1\"]/countryCode";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_commodity_netMass_shouldDedupe() {
        String pointer1 = BASE + "/commodity/netMass";
        String pointer2 = BASE + "/commodity/netMass";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_partiesBy_physicalAddress_streetAndNumber_shouldDedupe() {
        // TODO: streetAndNumber partially obscured - verify field name
        String pointer1 = BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/streetAndNumber";
        String pointer2 = BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/streetAndNumber";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_partiesBy_physicalAddress_cityName_shouldDedupe() {
        String pointer1 = BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/cityName";
        String pointer2 = BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/cityName";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    @Test
    void screenshot3_partiesBy_physicalAddress_zipCode_shouldDedupe() {
        String pointer1 = BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/zipCode";
        String pointer2 = BASE + "/parties[partyRoleType=\"BY\"]/physicalAddress/zipCode";

        List<RuleFiredValidationResult> results = results("BR255_666", pointer1, pointer2);
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
    }

    // -------------------------------------------------------------------------
    // Helpers - using real builder pattern from codebase
    // -------------------------------------------------------------------------

    private List<RuleFiredValidationResult> results(String ruleId, String pointer1, String pointer2) {
        return new ArrayList<>(List.of(
                result(ruleId, pointer1),
                result(ruleId, pointer2)
        ));
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
