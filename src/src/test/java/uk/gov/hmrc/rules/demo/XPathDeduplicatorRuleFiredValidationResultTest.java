package uk.gov.hmrc.rules.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests deduplication operating directly on RuleFiredValidationResult objects.
 *
 * Key scenarios covered:
 * - Dedup is scoped per rule ID
 * - Results for different rules interleaved in the list (non-sequential)
 * - Single pointer removed from a multi-pointer result
 * - Entire result removed when all pointers are dupes
 * - Order of remaining results preserved
 * - Different goods items within the same rule are NOT deduped
 */
class XPathDeduplicatorRuleFiredValidationResultTest {

    private static final String BASE_1 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"1\"]";
    private static final String BASE_2 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"2\"]";
    private static final String BASE_3 = "/declaration/consignmentShipment/goodsItems[sequenceNumber=\"3\"]";

    private XPathDeduplicator deduplicator;

    @BeforeEach
    void setUp() {
        deduplicator = new XPathDeduplicator(Map.of(
                "partyRoleType",         false,
                "countryRegionRoleType", true
        ));
    }

    // -------------------------------------------------------------------------
    // Core dedup within a single rule
    // -------------------------------------------------------------------------

    @Test
    void singleRule_duplicatePointers_shouldRemoveDuplicatePointer() {
        // BR255_666 fires on goodsItems 1 and 2 for the same field
        // Result 1 has pointer for goodsItem 1 - kept
        // Result 2 has pointer for goodsItem 2 - different goods item, kept
        // Result 3 has another pointer for goodsItem 1 same field - removed, result removed
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", BASE_1 + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType"),
                result("BR255_666", BASE_2 + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType"),
                result("BR255_666", BASE_1 + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/preferenceType") // dupe of result 1
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(2);
        assertThat(output.get(0).getPointers()).containsExactly(
                BASE_1 + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType"
        );
        assertThat(output.get(1).getPointers()).containsExactly(
                BASE_2 + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType"
        );
    }

    @Test
    void singleRule_resultWithMultiplePointers_onlyDupePointerRemoved() {
        // A single result has two pointers - one is a dupe, one is not
        // Only the dupe pointer should be removed, result should remain
        RuleFiredValidationResult result = resultWithPointers("BR255_666", new ArrayList<>(List.of(
                BASE_1 + "/procedureCombination/previousProcedureType",
                BASE_3 + "/commodity/grossMass"
        )));

        // Seed the seen set with one of the pointers by processing a prior result first
        RuleFiredValidationResult prior = result("BR255_666",
                BASE_1 + "/procedureCombination/previousProcedureType");

        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(prior, result));
        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        // prior result kept, second result kept but with only the non-dupe pointer
        assertThat(output).hasSize(2);
        assertThat(output.get(1).getPointers()).containsExactly(
                BASE_3 + "/commodity/grossMass"
        );
    }

    @Test
    void singleRule_allPointersAreDupes_entireResultRemoved() {
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", BASE_1 + "/invoiceValue"),
                result("BR255_666", BASE_1 + "/invoiceValue") // exact dupe
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(1);
        assertThat(output.get(0).getPointers()).containsExactly(BASE_1 + "/invoiceValue");
    }

    // -------------------------------------------------------------------------
    // Dedup is scoped per rule - different rules do NOT share a seen set
    // -------------------------------------------------------------------------

    @Test
    void differentRules_samePointer_shouldNotDedupe() {
        // Same pointer appearing in BR255_666 and BR455_202 - these are independent
        String pointer = BASE_1 + "/additionalDocuments[sequenceNumber=\"1\"]/type";

        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", pointer),
                result("BR455_202", pointer)
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(2);
        assertThat(output.get(0).getPointers()).containsExactly(pointer);
        assertThat(output.get(1).getPointers()).containsExactly(pointer);
    }

    // -------------------------------------------------------------------------
    // Non-sequential interleaving - as seen in screenshot 3
    // BR255_666, BR455_202, BR255_666 - rules are not grouped together
    // -------------------------------------------------------------------------

    @Test
    void interleavedRules_dedupStillScopedPerRule() {
        String pointer1 = BASE_1 + "/declaredDutyTaxFees[sequenceNumber=\"1\"]/preferenceType";
        String pointer2 = BASE_1 + "/additionalDocuments[sequenceNumber=\"1\"]/type";
        String pointer1Dupe = BASE_1 + "/declaredDutyTaxFees[sequenceNumber=\"2\"]/preferenceType"; // normalises same as pointer1

        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", pointer1),   // BR255_666 first occurrence
                result("BR455_202", pointer2),   // different rule sandwiched in between
                result("BR255_666", pointer1Dupe) // BR255_666 again - should be deduped against first BR255_666
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        // BR455_202 entry kept, third entry (BR255_666 dupe) removed entirely
        assertThat(output).hasSize(2);
        assertThat(output.get(0).getPointers()).containsExactly(pointer1);
        assertThat(output.get(1).getPointers()).containsExactly(pointer2);
    }

    @Test
    void interleavedRules_multipleInterleavings_orderPreserved() {
        String br255Pointer1 = BASE_1 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification";
        String br255Pointer1Dupe = BASE_1 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"2\"]/partyIdentification";
        String br455Pointer1 = BASE_1 + "/packaging[sequenceNumber=\"1\"]/quantity";
        String br455Pointer1Dupe = BASE_1 + "/packaging[sequenceNumber=\"2\"]/quantity";
        String br255Pointer2 = BASE_2 + "/parties[partyRoleType=\"LC\" and sequenceNumber=\"1\"]/partyIdentification"; // different goods item - kept

        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", br255Pointer1),     // pos 0 - kept
                result("BR455_202", br455Pointer1),     // pos 1 - kept
                result("BR255_666", br255Pointer1Dupe), // pos 2 - dupe of pos 0, removed
                result("BR455_202", br455Pointer1Dupe), // pos 3 - dupe of pos 1, removed
                result("BR255_666", br255Pointer2)      // pos 4 - different goods item, kept
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(3);
        assertThat(output.get(0).getPointers()).containsExactly(br255Pointer1);
        assertThat(output.get(1).getPointers()).containsExactly(br455Pointer1);
        assertThat(output.get(2).getPointers()).containsExactly(br255Pointer2);
    }

    // -------------------------------------------------------------------------
    // Different goods items within the same rule are NOT deduped
    // -------------------------------------------------------------------------

    @Test
    void sameRule_differentGoodsItems_shouldNotDedupe() {
        List<RuleFiredValidationResult> results = new ArrayList<>(List.of(
                result("BR255_666", BASE_1 + "/invoiceValue"),
                result("BR255_666", BASE_2 + "/invoiceValue"),
                result("BR255_666", BASE_3 + "/invoiceValue")
        ));

        List<RuleFiredValidationResult> output = deduplicator.deduplicate(results);

        assertThat(output).hasSize(3);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private RuleFiredValidationResult result(String ruleId, String pointer) {
        return resultWithPointers(ruleId, new ArrayList<>(List.of(pointer)));
    }

    private RuleFiredValidationResult resultWithPointers(String ruleId, List<String> pointers) {
        RuleFiredValidationResult result = new RuleFiredValidationResult();
        result.setRule(new ValidationRule(ruleId));
        result.setPointers(pointers);
        return result;
    }
}
