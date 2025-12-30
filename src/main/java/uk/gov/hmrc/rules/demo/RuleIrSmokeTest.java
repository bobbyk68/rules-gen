package uk.gov.hmrc.rules.demo;

import uk.gov.hmrc.rules.RuleRow;
import uk.gov.hmrc.rules.ir.ActionNode;
import uk.gov.hmrc.rules.ir.EmitErrorActionNode;
import uk.gov.hmrc.rules.ir.ConditionNode;
import uk.gov.hmrc.rules.ir.FactConditionNode;
import uk.gov.hmrc.rules.ir.ParentConditionNode;
import uk.gov.hmrc.rules.ir.RuleIrGenerator;
import uk.gov.hmrc.rules.ir.RuleModel;
import uk.gov.hmrc.rules.parsing.ConditionParser;
import uk.gov.hmrc.rules.parsing.ParsedCondition;
import uk.gov.hmrc.rules.parsing.TextConditionParser;

import java.util.ArrayList;
import java.util.List;

public class RuleIrSmokeTest {

    public static void main(String[] args) {

        ConditionParser parser = new TextConditionParser();
        RuleIrGenerator irGen = new RuleIrGenerator();

        List<RuleRow> rows = new ArrayList<>();
        rows.add(sampleSpAi());
        rows.add(sampleSpSpDifferentGi());
        rows.add(sampleAdDocAi());

        rows.add(new RuleRow(
                "BR675_1067",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.requestedProcedure.code equals A01",
                "all GoodsItem.previousProcedure.code must is one of B02,B03",
                "DMS12056",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1125",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B02",
                "all GoodsItem.specialProcedures.code must is not one of B03",
                "DMS12057",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1353",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one Declaration.AuthorizationHolder.authorizationType.code must equals EX",
                "DMS12058",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1029",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one Declaration.AuthorizationHolder.authorizationType.code equals EX",
                "at least one GoodsItem.additionalDocuments.type.code must equals C501",
                "DMS12059",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1231",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.additionalInformation.code must equals MOVE3",
                "DMS12060",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1337",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.declaredDutyTaxFees.preference.code equals 100",
                "at least one GoodsItem.Origin.subRole.code must equals CO",
                "DMS12061",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_217",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one ConsignmentShipment.valuationAdjustments.type.code equals A01",
                "all GoodsItem.valuationAdjustments.type.code must not equal B02",
                "DMS12062",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1186",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B01",
                "at least one GoodsItem.additionalDocuments.type.code must equals C624",
                "DMS12063",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1125",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B01",
                "at least one GoodsItem.specialProcedures.code must is one of B01,B02",
                "DMS12064",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1244",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.previousProcedure.code equals B01",
                "all GoodsItem.additionalDocuments.type.code must is not one of C501,C502",
                "DMS12065",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1281",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.exemption.code equals UE",
                "at least one GoodsItem.additionalDocuments.type.code must is one of C501,C624",
                "DMS12066",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1125",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B02",
                "at least one GoodsItem.specialProcedures.code must equals B02",
                "DMS12067",
                "BR675"
        ));


        // =======================================================
// MERGE #1
// IF : there is at least one GoodsItem.requestedProcedure.code equals
// THEN: all GoodsItem.previousProcedure.code must is one of
// =======================================================
        rows.add(new RuleRow(
                "BR675_MERGE_1",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.requestedProcedure.code equals A01",
                "all GoodsItem.previousProcedure.code must is one of B02,B03",
                "DMS12056",
                "BR675"
                ));

// =======================================================
// MERGE #2
// IF : there is at least one GoodsItem.additionalDocuments.exemption.code equals
// THEN: at least one GoodsItem.additionalDocuments.type.code must is one of
// =======================================================
        rows.add(new RuleRow(
                "BR675_MERGE_2",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.exemption.code equals EX1",
                "at least one GoodsItem.additionalDocuments.type.code must is one of AD1,AD2",
                "DMS12056",
                "BR675"
                ));

// =======================================================
// MERGE #3
// IF : there is at least one GoodsItem.valuationAdjustments.type.code equals
// THEN: all GoodsItem.valuationAdjustments.amount.value must is less than
// =======================================================
        rows.add(new RuleRow(
                "BR675_MERGE_3",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.valuationAdjustments.type.code equals VA1",
                "all GoodsItem.valuationAdjustments.amount.value must is less than 100",
                "DMS12056",
                "BR675"
        ));

// ======================================================
// Smoke rows derived from your screenshots (Pointers sheet)
// ID is taken from the BR675_xxxx column on the right.
// Values are dummy values just to exercise the parser.
// ======================================================


// ---------- Screenshot 1 (rows 2..19) ----------

        rows.add(new RuleRow(
                "BR675_1231",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.additionalInformation.code must equals MOVE3",
                "DMS_SMOKE_BR675_1231",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1067",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.requestedProcedure.code equals A01",
                "all GoodsItem.previousProcedure.code must is one of B01,B02",
                "DMS_SMOKE_BR675_1067",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1125",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "all GoodsItem.specialProcedures.code must is not one of 00A,00B",
                "DMS_SMOKE_BR675_1125",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1029",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one Declaration.AuthorizationHolder.authorizationType.code equals EX123",
                "at least one GoodsItem.additionalDocuments.type.code must equals C501",
                "DMS_SMOKE_BR675_1029",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1353",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one Declaration.AuthorizationHolder.authorizationType.code must equals EX123",
                "DMS_SMOKE_BR675_1353",
                "BR675"
        ));

// Row 7 (BR675_1337) in the screenshot shows THEN blank in the image → not generated.

        rows.add(new RuleRow(
                "BR675_217",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one ConsignmentShipment.valuationAdjustments.type.code equals VA1",
                "all GoodsItem.valuationAdjustments.type.code must not equal VA9",
                "DMS_SMOKE_BR675_217",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1186",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.additionalDocuments.type.code must equals C501",
                "DMS_SMOKE_BR675_1186",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1241",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals GEN1",
                "at least one GoodsItem.specialProcedures.code must equals 72M",
                "DMS_SMOKE_BR675_1241",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1240",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals GEN1",
                "at least one GoodsItem.specialProcedures.code must is one of 72M,73M",
                "DMS_SMOKE_BR675_1240",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1346",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals GEN1",
                "at least one GoodsItem.additionalDocuments.type.code must equals C501",
                "DMS_SMOKE_BR675_1346",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1317",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.specialProcedures.code must is one of 72M,73M",
                "DMS_SMOKE_BR675_1317",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1281",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.exemption.code equals EX9",
                "at least one GoodsItem.additionalDocuments.type.code must is one of C501,C502",
                "DMS_SMOKE_BR675_1281",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1514",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals GEN1",
                "at least one GoodsItem.additionalInformation.code must equals GEN2",
                "DMS_SMOKE_BR675_1514",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1030",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one Declaration.AuthorizationHolder.authorizationType.code must equals EX123",
                "DMS_SMOKE_BR675_1030",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1319",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.specialProcedures.code must equals 72M",
                "DMS_SMOKE_BR675_1319",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1244",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.previousProcedure.code equals B01",
                "all GoodsItem.additionalDocuments.type.code must is not one of C501,C502",
                "DMS_SMOKE_BR675_1244",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_1351",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.commodity.classifications.type must equals 10",
                "DMS_SMOKE_BR675_1351",
                "BR675"
        ));

// ---------- Screenshot 2 (rows 19..33) ----------
// Some BR675 ids are partially obscured by glare in the photo.
// Where I could not read the full ID confidently, I’ve used BR675_UNK_Rxx.
// Replace those with the exact BR675_#### from your sheet.

        rows.add(new RuleRow(
                "BR675_UNK_R20", // looked like BR675_119? in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.declaredDutyTaxFees.preference.code equals P1",
                "at least one GoodsItem.requestedProcedure.code must equals A01",
                "DMS_SMOKE_BR675_UNK_R20",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_757",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.declaredDutyTaxFees.preference.code equals P1",
                "all GoodsItem.Origin.subRole.code must not equal X1",
                "DMS_SMOKE_BR675_757",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R22", // looked like BR675_105? in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one ConsignmentShipment.previousDocuments.type.code must equals N380",
                "DMS_SMOKE_BR675_UNK_R22",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R23", // looked like BR675_122? in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one GoodsItem.specialProcedures.code must equals 72M",
                "DMS_SMOKE_BR675_UNK_R23",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R24", // looked like BR675_142? in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.additionalInformation.code must is one of GEN1,GEN2",
                "DMS_SMOKE_BR675_UNK_R24",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R25", // ID obscured by glare
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "all GoodsItem.specialProcedures.code must not equal 99Z",
                "DMS_SMOKE_BR675_UNK_R25",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R26", // looked like BR675_93?2 in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one GoodsItem.Origin.subRole.code must equals X1",
                "DMS_SMOKE_BR675_UNK_R26",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R27", // looked like BR675_43?2 in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalInformation.code equals GEN1",
                "at least one GoodsItem.requestedProcedure.code must equals A01",
                "DMS_SMOKE_BR675_UNK_R27",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R28", // looked like BR675_118? in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "all GoodsItem.additionalDocuments.type.code must not equal C999",
                "DMS_SMOKE_BR675_UNK_R28",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R29", // looked like BR675_13?? in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.invoiceAmount must is provided",
                "DMS_SMOKE_BR675_UNK_R29",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R30", // looked like BR675_158? in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "all GoodsItem.additionalInformation.code must not equal GEN9",
                "DMS_SMOKE_BR675_UNK_R30",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R31", // looked like BR675_164? in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.requestedProcedure.code equals A01",
                "all GoodsItem.previousProcedure.code must is not one of B01,B02",
                "DMS_SMOKE_BR675_UNK_R31",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R32", // looked like BR675_151? in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.valuationMethod.code equals 1",
                "all GoodsItem.specialProcedures.code must is not one of 72M,73M",
                "DMS_SMOKE_BR675_UNK_R32",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_UNK_R33", // looked like BR675_138? in the image
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.valuationAdjustments.type.code equals VA1",
                "all GoodsItem.valuationAdjustments.amount.value must is less than 100",
                "DMS_SMOKE_BR675_UNK_R33",
                "BR675"
        ));

        for (RuleRow row : rows) {
            System.out.println("==================================================");
            System.out.println("RuleRow id  : " + row.id());
            System.out.println("IF   (excel): " + row.ifCondition());
            System.out.println("THEN (excel): " + row.thenCondition());
            System.out.println();

            ParsedCondition ifCond   = parser.parseIf(row.ifCondition());
            ParsedCondition thenCond = parser.parseThen(row.thenCondition());

            RuleModel model = irGen.generate(row, List.of(ifCond, thenCond));

            debugPrintParsedConditions(ifCond, thenCond);
            debugPrintIr(model);
            System.out.println();
        }
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

    private static RuleRow sampleAdDocAi() {
        return new RuleRow(
            "BR675_9999",
            List.of("all"),
            List.of("C211"),
            "there is at least one GoodsItem.additionalDocuments.type.code must equals AD1",
            "at least one GoodsItem.additionalInformation.code must equals INFO1",
            "DMS99999",
            "BR675"
        );
    }

    private static void debugPrintParsedConditions(ParsedCondition ifCond,
                                                   ParsedCondition thenCond) {
        System.out.println("Parsed IF condition:");
        debugPrintParsedCondition(ifCond);
        System.out.println();

        System.out.println("Parsed THEN condition:");
        debugPrintParsedCondition(thenCond);
        System.out.println();
    }

    private static void debugPrintParsedCondition(ParsedCondition pc) {
        System.out.println("  entityType      = " + pc.getEntityType());
        System.out.println("  parentAnchorKey = " + pc.getParentAnchorKey());
        System.out.println("  fieldName       = " + pc.getFieldName());
        System.out.println("  operator        = " + pc.getOperator());
        System.out.println("  values          = " + pc.getValues());
        System.out.println("  fieldTypeLabel  = " + pc.getFieldTypeLabel());
        System.out.println("  role            = " + pc.getRole());
        System.out.println("  canonical       = " + pc.canonicalPathKey());
    }

    private static void debugPrintIr(RuleModel model) {
        System.out.println("IR conditions:");
        for (ConditionNode node : model.getConditions()) {
            if (node instanceof ParentConditionNode parent) {
                System.out.println("  Parent: alias=" + parent.getAlias()
                    + ", factType=" + parent.getFactType()
                    + ", constraints=" + parent.getFieldConstraints());
            } else if (node instanceof FactConditionNode fact) {
                System.out.println("  Child:  alias=" + fact.getAlias()
                    + ", factType=" + fact.getFactType()
                    + ", parentAlias=" + fact.getParentAlias()
                    + ", role=" + fact.getRole()
                    + ", label=" + fact.getFieldTypeLabel()
                    + ", constraints=" + fact.getFieldConstraints());
            }
        }

        System.out.println("Actions:");
        for (ActionNode a : model.getActions()) {
            if (a instanceof EmitErrorActionNode emit) {
                System.out.println("  Emit BR=" + emit.getBrCode()
                    + ", DMS=" + emit.getDmsErrorCode());
            }
        }
    }
}
