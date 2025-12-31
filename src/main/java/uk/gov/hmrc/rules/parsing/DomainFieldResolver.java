package uk.gov.hmrc.rules.parsing;


/**
 * Maps canonical paths like "GoodsItem.specialProcedures.code"
 * to entityType, anchor, fieldName, and a human label.
 *
 * This replaces your JSON combo table – you only maintain
 * field-level metadata here, not IF/THEN combos.
 */
public class DomainFieldResolver {

    public DomainFieldDescriptor resolve(String canonicalPath) {
q        String path = canonicalPath.trim();

        if (path.equalsIgnoreCase("GoodsItem.specialProcedures.code")) {
            return new DomainFieldDescriptor(
                    "SpecialProcedure",
                    "GI",
                    "code",
                    "special procedure"
            );
        }

        if (path.equalsIgnoreCase("GoodsItem.previousProcedure.code")) {
            return new DomainFieldDescriptor(
                    "GoodsItem",
                    "GI",
                    "previousProcedureCode",
                    "previous procedure"
            );
        }

        if (path.equalsIgnoreCase("GoodsItem.requestedProcedure.code")) {
            return new DomainFieldDescriptor(
                    "GoodsItem",
                    "GI",
                    "requestedProcedureCode",
                    "requested procedure"
            );
        }

        if (path.equalsIgnoreCase("GoodsItem.additionalInformation.code")) {
            return new DomainFieldDescriptor(
                    "AdditionalInformation",
                    "GI",
                    "code",
                    "additional information"
            );
        }

        if (path.equalsIgnoreCase("GoodsItem.additionalDocuments.type.code")) {
            return new DomainFieldDescriptor(
                    "AdditionalDocument",
                    "GI",
                    "typeCode",
                    "additional document type"
            );
        }

        if (path.equalsIgnoreCase("GoodsItem.valuationAdjustments.type.code")) {
            return new DomainFieldDescriptor(
                    "ValuationAdjustment",
                    "GI",
                    "typeCode",
                    "valuation adjustment type"
            );
        }

        if (path.equalsIgnoreCase("GoodsItem.valuationAdjustments.amount.value")) {
            return new DomainFieldDescriptor(
                    "ValuationAdjustment",
                    "GI",
                    "amountValue",
                    "valuation adjustment amount"
            );
        }

        if (path.equalsIgnoreCase("GoodsItem.additionalDocuments.exemption.code")) {
            return new DomainFieldDescriptor(
                    "AdditionalDocument",
                    "GI",
                    "exemptionCode",
                    "additional document exemption code"
            );
        }

        if (path.equalsIgnoreCase("Declaration.AuthorizationHolder.authorizationType.code")) {
            return new DomainFieldDescriptor(
                    "AuthorizationHolder",
                    "DECL",
                    "authorizationTypeCode",
                    "authorization type"
            );
        }

        if (path.equalsIgnoreCase("Declaration.type.code")) {
            return new DomainFieldDescriptor(
                    "Declaration",
                    "DECL",
                    "typeCode",
                    "declaration type"
            );
        }

        if (path.equalsIgnoreCase("GoodsItem.declaredDutyTaxFees.preference.code")) {
            return new DomainFieldDescriptor(
                    "DeclaredDutyTaxFee",
                    "GI",
                    "preferenceCode",
                    "declared duty tax fee preference"
            );
        }

        if (path.equalsIgnoreCase("GoodsItem.Origin.subRole.code")) {
            return new DomainFieldDescriptor(
                    "Origin",
                    "GI",
                    "subRoleCode",
                    "origin sub role"
            );
        }

        if (path.equalsIgnoreCase("ConsignmentShipment.valuationAdjustments.type.code")) {
            return new DomainFieldDescriptor(
                    "ValuationAdjustment",
                    "CON",
                    "typeCode",
                    "consignment valuation adjustment type"
            );
        }



        // Fallback: not recognised – still usable for debugging
        return new DomainFieldDescriptor(
                "UnknownEntity",
                "GI",
                lastSegment(path),
                lastSegment(path)
        );
    }

    //GoodsItem.valuationAdjustments.type.code
    // GoodsItem.valuationAdjustments.amount.value

    private String lastSegment(String path) {
        int idx = path.lastIndexOf('.');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }
}
