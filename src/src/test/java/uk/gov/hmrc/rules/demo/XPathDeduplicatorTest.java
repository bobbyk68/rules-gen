package uk.gov.hmrc.rules.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Core deduplication tests using real RuleFiredValidationResult objects via builder pattern.
 *
 * @version 0.2.0-SNAPSHOT
 * @since 2026-03-02
 */
class XPathDeduplicatorTest {

    private static final String BASE_1 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]";
    private static final String BASE_2 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"2\"]";
    private static final String BASE_3 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"3\"]";

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
    // Core dedup - same rule, same goods item, different sequence numbers in tail
    // -------------------------------------------------------------------------

    @Test
    void sameRule_sameGoodsItem_differentTailSequenceNumbers_shouldDedupe() {
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
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
    void sameRule_differentGoodsItems_shouldNotDedupe() {
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", BASE_1 + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType"),
                result("BR255_666", BASE_2 + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType"),
                result("BR255_666", BASE_3 + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType")
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(3);
    }

    @Test
    void differentRules_samePointer_shouldNotDedupe() {
        String pointer = BASE_1 + "/additionalInformation[sequenceNumber=\"1\"]/code";

        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", pointer),
                result("BR455_202", pointer)
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(2);
    }

    // -------------------------------------------------------------------------
    // Interleaved rules - BR255_666, BR455_202, BR255_666
    // -------------------------------------------------------------------------

    @Test
    void interleavedRules_dedupScopedPerRule_orderPreserved() {
        String br255Pointer1     = BASE_1 + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType";
        String br455Pointer1     = BASE_1 + "/additionalDocuments[sequenceNumber=\"1\"]/type";
        String br255Pointer1Dupe = BASE_1 + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/preferenceType";

        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", br255Pointer1),
                result("BR455_202", br455Pointer1),
                result("BR255_666", br255Pointer1Dupe)
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(2);
        assertThat(output.get(0).getPointers()).containsExactly(br255Pointer1);
        assertThat(output.get(1).getPointers()).containsExactly(br455Pointer1);
    }

    // -------------------------------------------------------------------------
    // Result with multiple pointers - only dupe pointer removed, result stays
    // -------------------------------------------------------------------------

    @Test
    void resultWithMultiplePointers_onlyDupePointerRemoved() {
        RuleFiredValidationResult prior = result("BR255_666",
                BASE_1 + "/procedureCombination/previousProcedureType");

        RuleFiredValidationResult multiPointer = resultWithPointers("BR255_666", new ArrayList<>(List.of(
                BASE_1 + "/procedureCombination/previousProcedureType", // dupe of prior
                BASE_3 + "/commodity/grossMass"                          // unique - kept
        )));

        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(prior, multiPointer));
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(2);
        assertThat(output.get(1).getPointers()).containsExactly(BASE_3 + "/commodity/grossMass");
    }

    @Test
    void allPointersDeduped_entireResultRemoved() {
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", BASE_1 + "/invoiceValue"),
                result("BR255_666", BASE_1 + "/invoiceValue")
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
    }

    // -------------------------------------------------------------------------
    // Order preservation
    // -------------------------------------------------------------------------

    @Test
    void orderPreserved_acrossMultipleRulesAndDedupes() {
        String br255p1     = BASE_1 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification";
        String br255p1Dupe = BASE_1 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]/partyIdentification";
        String br455p1     = BASE_1 + "/packaging[sequenceNumber=\"1\"]/quantity";
        String br455p1Dupe = BASE_1 + "/packaging[sequenceNumber=\"2\"]/quantity";
        String br255p2     = BASE_2 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification";

        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", br255p1),
                result("BR455_202", br455p1),
                result("BR255_666", br255p1Dupe),
                result("BR455_202", br455p1Dupe),
                result("BR255_666", br255p2)
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(3);
        assertThat(output.get(0).getPointers()).containsExactly(br255p1);
        assertThat(output.get(1).getPointers()).containsExactly(br455p1);
        assertThat(output.get(2).getPointers()).containsExactly(br255p2);
    }

    // -------------------------------------------------------------------------
    // partyRoleType value matters - LC vs CN should not dedupe
    // -------------------------------------------------------------------------

    @Test
    void partiesLcVsCn_shouldNotDedupe() {
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", BASE_1 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification"),
                result("BR255_666", BASE_1 + "/parties[partyRoleType=\"CN\" and sequenceNumber=\"1\"]/partyIdentification")
        ));

        assertThat(deduplicator.deduplicate(results)).hasSize(2);
    }

    // -------------------------------------------------------------------------
    // countryRegionRoleType value does not matter - different values should dedupe
    // -------------------------------------------------------------------------

    @Test
    void countryRegionRoles_differentRoleTypeValues_shouldDedupe() {
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", BASE_1 + "/countryRegionRoles[countryRegionRoleType=\"3\" and sequenceNumber=\"1\"]/countryCode"),
                result("BR255_666", BASE_1 + "/countryRegionRoles[countryRegionRoleType=\"5\" and sequenceNumber=\"1\"]/countryCode")
        ));

        assertThat(deduplicator.deduplicate(results)).hasSize(1);
    }

    // -------------------------------------------------------------------------
    // No results with empty pointer lists in output
    // -------------------------------------------------------------------------

    @Test
    void outputNeverContainsResultWithEmptyPointers() {
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", BASE_1 + "/invoiceValue"),
                result("BR255_666", BASE_1 + "/invoiceValue"),
                result("BR255_666", BASE_1 + "/invoiceValue")
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).allSatisfy(r -> assertThat(r.getPointers()).isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // Helpers - using real builder pattern from codebase
    // -------------------------------------------------------------------------

    private RuleFiredValidationResult result(String ruleId, String pointer) {
        return resultWithPointers(ruleId, new ArrayList<>(List.of(pointer)));
    }

    private RuleFiredValidationResult resultWithPointers(String ruleId, List<String> pointers) {
        ValidationResultContext context = ValidationResultContext.of()
                .ruleId(ruleId)
                .errorCode("DMS10002")
                .errorMessage("Obligation Error: Data Element is not allowed")
                .pointers(pointers)
                .goodsItemSequenceNumber(1)
                .build();

        return (RuleFiredValidationResult) ValidationResultBuilder.build(context);
    }
}
