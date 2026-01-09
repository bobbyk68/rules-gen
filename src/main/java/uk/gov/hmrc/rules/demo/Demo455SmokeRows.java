package uk.gov.hmrc.rules.demo;

import uk.gov.hmrc.rules.RuleRow;

import java.util.ArrayList;
import java.util.List;

public final class Demo455SmokeRows {

    private Demo455SmokeRows() {
    }

    public static List<RuleRow> create() {
        // KEEP YOUR EXISTING ROWS HERE.
        // If your current RuleRow constructor requires RuleSet, then include it here directly.
        // If not, keep legacy creation and let DemoSmokeTestResolver add it.

        List<RuleRow> rows = new ArrayList<>();
// =========================================================
// BR455 rows (generated from your screenshots)
// =========================================================

        rows.add(new RuleRow("BR455_005", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.BorderTransportMeans.mode.code must exist in list TransportModeTypes Else invalid",
                "emit BR455 BorderTransportMeans.mode.code must exist in list TransportModeTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_006", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.ArrivalTransportMeans.mode.code must exist in list PreBrexitTransportModeTypes Else invalid",
                "emit BR455 ArrivalTransportMeans.mode.code must exist in list PreBrexitTransportModeTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_013", List.of("ALL"), List.of("ALL"),
                "The value in Declaration.invoiceAmount.unitType.code must exist in list CurrencyTypes Else invalid",
                "emit BR455 invoiceAmount.unitType.code must exist in list CurrencyTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_022", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.previousDocuments.category.code must exist in list DocumentCategories Else invalid",
                "emit BR455 previousDocuments.category.code must exist in list DocumentCategories",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_026", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.packaging.type.code must exist in list PackageTypes Else invalid",
                "emit BR455 packaging.type.code must exist in list PackageTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_027", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.customsWarehouse.warehouseType.code must exist in list CustomsWarehouseTypes Else invalid",
                "emit BR455 customsWarehouse.warehouseType.code must exist in list CustomsWarehouseTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_030", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.tradeTerms.type.code must exist in list IncoTermCodes Else invalid",
                "emit BR455 tradeTerms.type.code must exist in list IncoTermCodes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_033", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.BorderTransportMeans.nationality.code must exist in list TransportNationalities Else invalid",
                "emit BR455 BorderTransportMeans.nationality.code must exist in list TransportNationalities",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_034", List.of("ALL"), List.of("ALL"),
                "The value in Declaration.invoiceAmount.unitType.code must exist in list CurrencyTypes Else invalid",
                "emit BR455 invoiceAmount.unitType.code must exist in list CurrencyTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_035", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.transactionNature.code must exist in list TransactionNatureTypes Else invalid",
                "emit BR455 transactionNature.code must exist in list TransactionNatureTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_036", List.of("ALL"), List.of("ALL"),
                "The value in Declaration.ExitOffice.customsOfficeID.number.identifier must exist in list ExitCustomsOffices Else invalid",
                "emit BR455 ExitOffice.customsOfficeID.number.identifier must exist in list ExitCustomsOffices",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_040", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.declaredDutyTaxFees.preference.code must exist in list PreferenceTypes Else invalid",
                "emit BR455 declaredDutyTaxFees.preference.code must exist in list PreferenceTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_042", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.declaredDutyTaxFees.methodOfPayment.code must exist in list PaymentMethodTypes Else invalid",
                "emit BR455 declaredDutyTaxFees.methodOfPayment.code must exist in list PaymentMethodTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_044", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.commodity.classifications.type.code must exist in list CommodityClassificationTypes Else invalid",
                "emit BR455 commodity.classifications.type.code must exist in list CommodityClassificationTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_045", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.valuationAdjustments.type.code must exist in list GoodsItemValuationAdjustmentTypes Else invalid",
                "emit BR455 valuationAdjustments.type.code must exist in list GoodsItemValuationAdjustmentTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_059", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.declaredDutyTaxFees.specificBase.unitType.code must exist in list TaxBaseMeasureUnitTypes Else invalid",
                "emit BR455 declaredDutyTaxFees.specificBase.unitType.code must exist in list TaxBaseMeasureUnitTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_104", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.valuationAdjustments.type.code must exist in list HeaderValuationAdjustmentTypes Else invalid",
                "emit BR455 valuationAdjustments.type.code must exist in list HeaderValuationAdjustmentTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_105", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.valuationMethod.code must exist in list ValuationMethodTypes Else invalid",
                "emit BR455 valuationMethod.code must exist in list ValuationMethodTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_106", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.ExportationCountry.country.code must exist in list EuCountries Else invalid",
                "emit BR455 ExportationCountry.country.code must exist in list EuCountries",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_119", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.previousDocuments.type.code must exist in list ExportPreviousDocuments Else invalid",
                "emit BR455 previousDocuments.type.code must exist in list ExportPreviousDocuments",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_120", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.previousDocuments.type.code must exist in list ExportItemPreviousDocuments Else invalid",
                "emit BR455 previousDocuments.type.code must exist in list ExportItemPreviousDocuments",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_125", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.GoodsLocation.locationIdentificationType must exist in list LocationIdentificationTypes Else invalid",
                "emit BR455 GoodsLocation.locationIdentificationType must exist in list LocationIdentificationTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_126", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.GoodsLocation.locationType must exist in list LocationTypes Else invalid",
                "emit BR455 GoodsLocation.locationType must exist in list LocationTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_127", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.ArrivalTransportMeans.transportMeansIdentificationType.code must exist in list TransportMeansIdentificationTypes Else invalid",
                "emit BR455 ArrivalTransportMeans.transportMeansIdentificationType.code must exist in list TransportMeansIdentificationTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_128", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.Origin.subRole.code must exist in list CountryRegionSubRoleTypes Else invalid",
                "emit BR455 Origin.subRole.code must exist in list CountryRegionSubRoleTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_129", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.transactionNature.code must exist in list TransactionNatureTypes Else invalid",
                "emit BR455 transactionNature.code must exist in list TransactionNatureTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_130", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.valuationIndicator.code must exist in list ValuationIndicatorTypes Else invalid",
                "emit BR455 valuationIndicator.code must exist in list ValuationIndicatorTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_136", List.of("ALL"), List.of("ALL"),
                "The value in Declaration.SupervisingOffice.customsOfficeID.number.identifier must exist in list SupervisingCustomsOffices Else invalid",
                "emit BR455 SupervisingOffice.customsOfficeID.number.identifier must exist in list SupervisingCustomsOffices",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_137", List.of("ALL"), List.of("ALL"),
                "The value in Declaration.guarantees.type.code must exist in list DeclaredGuaranteeTypes Else invalid",
                "emit BR455 guarantees.type.code must exist in list DeclaredGuaranteeTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_138", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.additionalDocuments.exemption.code must exist in list DocumentExemptionTypes Else invalid",
                "emit BR455 additionalDocuments.exemption.code must exist in list DocumentExemptionTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_139", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.LoadingLocation.locationAdditionalId must exist in list AirportCodes Else invalid",
                "emit BR455 LoadingLocation.locationAdditionalId must exist in list AirportCodes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_140", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.DepartureTransportMeans.identificationType.code must exist in list TransportMeansIdentificationTypes Else invalid",
                "emit BR455 DepartureTransportMeans.identificationType.code must exist in list TransportMeansIdentificationTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_150", List.of("ALL"), List.of("ALL"),
                "The value in Declaration.AuthorizationHolder.authorizationType.code must exist in list PartyRoleAuthorizationTypes Else invalid",
                "emit BR455 AuthorizationHolder.authorizationType.code must exist in list PartyRoleAuthorizationTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_151", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.previousDocuments.category.code must exist in list DocumentCategories Else invalid",
                "emit BR455 previousDocuments.category.code must exist in list DocumentCategories",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_152", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.additionalDocuments.quantity.measureUnitType must exist in list MeasureUnitTypes Else invalid",
                "emit BR455 additionalDocuments.quantity.measureUnitType must exist in list MeasureUnitTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_154", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.specialProcedures.code must exist in list ImportSpecialProcedures Else invalid",
                "emit BR455 specialProcedures.code must exist in list ImportSpecialProcedures",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_156", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.valuationAdjustments.amount.unitType.code must exist in list CurrencyTypes Else invalid",
                "emit BR455 valuationAdjustments.amount.unitType.code must exist in list CurrencyTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_160", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.additionalInformation.code must exist in list ImportSpecialMentions Else invalid",
                "emit BR455 additionalInformation.code must exist in list ImportSpecialMentions",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_161", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.invoiceAmount.unitType.code must exist in list CurrencyTypes Else invalid",
                "emit BR455 invoiceAmount.unitType.code must exist in list CurrencyTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_169", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.ExportationCountry.country.code must exist in list CurrencyTypes Else invalid",
                "emit BR455 ExportationCountry.country.code must exist in list CurrencyTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_170", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.ExportationCountry.country.code must exist in list ImportCountries Else invalid",
                "emit BR455 ExportationCountry.country.code must exist in list ImportCountries",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_171", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.declaredDutyTaxFees.methodOfPayment.code must exist in list DeferredPaymentMethods Else invalid",
                "emit BR455 declaredDutyTaxFees.methodOfPayment.code must exist in list DeferredPaymentMethods",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_173", List.of("ALL"), List.of("ALL"),
                "The value in Declaration.guarantees.dutyAmount.unitType.code must exist in list CurrencyTypes Else invalid",
                "emit BR455 guarantees.dutyAmount.unitType.code must exist in list CurrencyTypes",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_182", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.additionalDocuments.type.code must not exist in list ALVSDocuments Else invalid",
                "emit BR455 additionalDocuments.type.code must not exist in list ALVSDocuments",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_309", List.of("ALL"), List.of("ALL"),
                "The value in GoodsItem.Origin.country.code must exist in list ImportCountries Else invalid",
                "emit BR455 Origin.country.code must exist in list ImportCountries",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_310", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.BorderTransportMeans.nationality.code must exist in list ImportCountries Else invalid",
                "emit BR455 BorderTransportMeans.nationality.code must exist in list ImportCountries",
                "DMS10020", "BR455 demo"));

        rows.add(new RuleRow("BR455_311", List.of("ALL"), List.of("ALL"),
                "The value in ConsignmentShipment.previousDocuments.type.code must not exist in list ILIDocuments Else invalid",
                "emit BR455 previousDocuments.type.code must not exist in list ILIDocuments",
                "DMS10020", "BR455 demo"));


        return rows;
    }
}