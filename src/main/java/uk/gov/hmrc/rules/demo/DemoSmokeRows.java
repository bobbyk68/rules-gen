package uk.gov.hmrc.rules.demo;

import java.util.ArrayList;
import java.util.List;

import uk.gov.hmrc.rules.RuleRow;

public final class DemoSmokeRows {

    private DemoSmokeRows() {
    }

    public static List<RuleRow> create() {
        // KEEP YOUR EXISTING ROWS HERE.
        // If your current RuleRow constructor requires RuleSet, then include it here directly.
        // If not, keep legacy creation and let DemoSmokeTestResolver add it.

        List<RuleRow> rows = new ArrayList<>();


        rows.add(new RuleRow(
                "BR675_1125",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B02",
                "all GoodsItem.specialProcedures.code must is not one of B03",
                "DMS12057",
                "BR675"
        ));
        rows.add(sampleSpAi());
        rows.add(new RuleRow(
                "BR675_1067",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.requestedProcedure.code equals A01",
                "all GoodsItem.previousProcedure.code must is one of B02,B03",
                "DMS12056",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1335",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.invoiceAmount must is provided",
                "DMS_SMOKE_BR675_UNK_R29",
                "BR675"
        ));
        rows.add(sampleSpSpDifferentGi());
        rows.add(sampleAiDocAd());
        addRows(rows);
        return rows;
    }



    private static RuleRow sampleSpAi() {
        return new RuleRow(
                "BR675_1231",
                List.of("J", "F", "C"),
                List.of("C211", "C21E"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.additionalInformation.code must equals MOVE3",
                "DMS12056",
                "BR675"
        );
    }

    private static RuleRow sampleSpSpDifferentGi() {
        return new RuleRow(
                "BR675_1125",
                List.of("all"),
                List.of("C211", "H1", "H2", "H3", "H4", "H5", "I1", "C21IEIDR"),
                "there is at least one GoodsItem.specialProcedures.code equals B02",
                "at least one GoodsItem.specialProcedures.code must be one of B03",
                "DMS12056",
                "BR675"
        );
    }

    private static RuleRow sampleAiDocAd() {
        return new RuleRow(
                "BR675_1346",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals INFO1",
                "at least one GoodsItem.additionalDocuments.type.code must equals AD1",
                "DMS99999",
                "BR675"
        );
    }

    private static void addRows(List<RuleRow> rows) {
        // --- Your original set (kept) ---





        rows.add(new RuleRow(
                "BR675_1353",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one Declaration.AuthorizationHolder.authorizationType.code must equals EX",
                "DMS12058",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1029",
                List.of("all"),
                List.of("C211"),
                "there is at least one Declaration.AuthorizationHolder.authorizationType.code equals EX",
                "at least one GoodsItem.additionalDocuments.type.code must equals C501",
                "DMS12059",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1231",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.additionalInformation.code must equals MOVE3",
                "DMS12060",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1337",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.declaredDutyTaxFees.preference.code equals 100",
                "at least one GoodsItem.Origin.subRole.code must equals CO",
                "DMS12061",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_217",
                List.of("all"),
                List.of("C211"),
                "there is at least one ConsignmentShipment.valuationAdjustments.type.code equals A01",
                "all GoodsItem.valuationAdjustments.type.code must not equal B02",
                "DMS12062",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1186",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B01",
                "at least one GoodsItem.additionalDocuments.type.code must equals C624",
                "DMS12063",
                "BR675"
        ));

// NOTE: BR675_1125 appears multiple times in your pasted list with different IF/THEN.
// Keeping them as separate smoke cases by suffixing.
        rows.add(new RuleRow(
                "BR675_1125_A",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B01",
                "at least one GoodsItem.specialProcedures.code must is one of B01,B02",
                "DMS12064",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1244",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.previousProcedure.code equals B01",
                "all GoodsItem.additionalDocuments.type.code must is not one of C501,C502",
                "DMS12065",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1281",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.exemption.code equals UE",
                "at least one GoodsItem.additionalDocuments.type.code must is one of C501,C624",
                "DMS12066",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1125_B",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B02",
                "at least one GoodsItem.specialProcedures.code must equals B02",
                "DMS12067",
                "BR675"
        ));

// --- Your MERGE rows (kept unless exact duplicate) ---
// MERGE_1 was an exact duplicate of BR675_1067 -> removed.

        rows.add(new RuleRow(
                "BR675_MERGE_2",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.exemption.code equals EX1",
                "at least one GoodsItem.additionalDocuments.type.code must is one of AD1,AD2",
                "DMS12056",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_3",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.valuationAdjustments.type.code equals VA1",
                "all GoodsItem.valuationAdjustments.amount.value must is less than 100",
                "DMS12056",
                "BR675"
        ));

// --- Screenshot 1 smoke rows (only those NOT exact-duplicated by your originals) ---

        rows.add(new RuleRow(
                "BR675_1067_SMOKE",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.requestedProcedure.code equals A01",
                "all GoodsItem.previousProcedure.code must is one of B01,B02",
                "DMS_SMOKE_BR675_1067",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1125_SMOKE",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "all GoodsItem.specialProcedures.code must is not one of 00A,00B",
                "DMS_SMOKE_BR675_1125",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1029_SMOKE",
                List.of("all"),
                List.of("C211"),
                "there is at least one Declaration.AuthorizationHolder.authorizationType.code equals EX123",
                "at least one GoodsItem.additionalDocuments.type.code must equals C501",
                "DMS_SMOKE_BR675_1029",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1353_SMOKE",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one Declaration.AuthorizationHolder.authorizationType.code must equals EX123",
                "DMS_SMOKE_BR675_1353",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_217_SMOKE",
                List.of("all"),
                List.of("C211"),
                "there is at least one ConsignmentShipment.valuationAdjustments.type.code equals VA1",
                "all GoodsItem.valuationAdjustments.type.code must not equal VA9",
                "DMS_SMOKE_BR675_217",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1186_SMOKE",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.additionalDocuments.type.code must equals C501",
                "DMS_SMOKE_BR675_1186",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1241",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals GEN1",
                "at least one GoodsItem.specialProcedures.code must equals 72M",
                "DMS_SMOKE_BR675_1241",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1240",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals GEN1",
                "at least one GoodsItem.specialProcedures.code must is one of 72M,73M",
                "DMS_SMOKE_BR675_1240",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1346",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals GEN1",
                "at least one GoodsItem.additionalDocuments.type.code must equals C501",
                "DMS_SMOKE_BR675_1346",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1317",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.specialProcedures.code must is one of 72M,73M",
                "DMS_SMOKE_BR675_1317",
                "BR675"
        ));

// NOTE: BR675_1281 already used above (UE/C501,C624). Keep this as a separate smoke by suffix.
        rows.add(new RuleRow(
                "BR675_1281_SMOKE",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.exemption.code equals EX9",
                "at least one GoodsItem.additionalDocuments.type.code must is one of C501,C502",
                "DMS_SMOKE_BR675_1281",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1514",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals GEN1",
                "at least one GoodsItem.additionalInformation.code must equals GEN2",
                "DMS_SMOKE_BR675_1514",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1030",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one Declaration.AuthorizationHolder.authorizationType.code must equals EX123",
                "DMS_SMOKE_BR675_1030",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1319",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.specialProcedures.code must equals 72M",
                "DMS_SMOKE_BR675_1319",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1351",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.commodity.classifications.type must equals 10",
                "DMS_SMOKE_BR675_1351",
                "BR675"
        ));

// --- Screenshot 2 smoke rows (kept as-is, with UNK ids) ---

        rows.add(new RuleRow(
                "BR675_UNK_R20",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.declaredDutyTaxFees.preference.code equals P1",
                "at least one GoodsItem.requestedProcedure.code must equals A01",
                "DMS_SMOKE_BR675_UNK_R20",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_757",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.declaredDutyTaxFees.preference.code equals P1",
                "all GoodsItem.Origin.subRole.code must not equal X1",
                "DMS_SMOKE_BR675_757",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R22",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one ConsignmentShipment.previousDocuments.type.code must equals N380",
                "DMS_SMOKE_BR675_UNK_R22",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R23",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one GoodsItem.specialProcedures.code must equals 72M",
                "DMS_SMOKE_BR675_UNK_R23",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R24",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.additionalInformation.code must is one of GEN1,GEN2",
                "DMS_SMOKE_BR675_UNK_R24",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R25",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "all GoodsItem.specialProcedures.code must not equal 99Z",
                "DMS_SMOKE_BR675_UNK_R25",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R26",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one GoodsItem.Origin.subRole.code must equals X1",
                "DMS_SMOKE_BR675_UNK_R26",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R27",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals GEN1",
                "at least one GoodsItem.requestedProcedure.code must equals A01",
                "DMS_SMOKE_BR675_UNK_R27",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R28",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "all GoodsItem.additionalDocuments.type.code must not equal C999",
                "DMS_SMOKE_BR675_UNK_R28",
                "BR675"
        ));



        rows.add(new RuleRow(
                "BR675_UNK_R30",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "all GoodsItem.additionalInformation.code must not equal GEN9",
                "DMS_SMOKE_BR675_UNK_R30",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R31",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.requestedProcedure.code equals A01",
                "all GoodsItem.previousProcedure.code must is not one of B01,B02",
                "DMS_SMOKE_BR675_UNK_R31",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R32",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.valuationMethod.code equals 1",
                "all GoodsItem.specialProcedures.code must is not one of 72M,73M",
                "DMS_SMOKE_BR675_UNK_R32",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R33",
                List.of("all"),
                List.of("C211"),
                "there is at least one GoodsItem.valuationAdjustments.type.code equals VA1",
                "all GoodsItem.valuationAdjustments.amount.value must is less than 100",
                "DMS_SMOKE_BR675_UNK_R33",
                "BR675"
        ));
    }

}
