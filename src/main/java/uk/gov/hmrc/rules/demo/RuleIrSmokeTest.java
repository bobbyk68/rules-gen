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
                "BR675_MERGE_1",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.requestedProcedure.code equals A01",
                "all GoodsItem.previousProcedure.code must is one of B02,B03",
                "DMS12056",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_2",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B02",
                "all GoodsItem.specialProcedures.code must is not one of B03",
                "DMS12057",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_3",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.type.code equals C501",
                "at least one Declaration.AuthorizationHolder.authorizationType.code must equals EX",
                "DMS12058",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_4",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one Declaration.AuthorizationHolder.authorizationType.code equals EX",
                "at least one GoodsItem.additionalDocuments.type.code must equals C501",
                "DMS12059",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_5",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals 72M",
                "at least one GoodsItem.additionalInformation.code must equals MOVE3",
                "DMS12060",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_6",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.declaredDutyTaxFees.preference.code equals 100",
                "at least one GoodsItem.Origin.subRole.code must equals CO",
                "DMS12061",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_7",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one ConsignmentShipment.valuationAdjustments.type.code equals A01",
                "all GoodsItem.valuationAdjustments.type.code must not equal B02",
                "DMS12062",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_8",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B01",
                "at least one GoodsItem.additionalDocuments.type.code must equals C624",
                "DMS12063",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_9",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B01",
                "at least one GoodsItem.specialProcedures.code must is one of B01,B02",
                "DMS12064",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_10",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.previousProcedure.code equals B01",
                "all GoodsItem.additionalDocuments.type.code must is not one of C501,C502",
                "DMS12065",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_11",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.additionalDocuments.exemption.code equals UE",
                "at least one GoodsItem.additionalDocuments.type.code must is one of C501,C624",
                "DMS12066",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_12",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one GoodsItem.specialProcedures.code equals B02",
                "at least one GoodsItem.specialProcedures.code must equals B02",
                "DMS12067",
                "BR675"
        ));

        rows.add(new RuleRow(
                "BR675_MERGE_13",
                java.util.List.of("all"),
                java.util.List.of("C211"),
                "there is at least one Declaration.type.code equals IM",
                "all GoodsItem.previousProcedure.code must is one of B01,B02",
                "DMS12068",
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
