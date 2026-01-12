package uk.gov.hmrc.rules.br455.lookup;

public enum Leaf {

    // --- core / generic leaves (very common) ---
    BASE(""),
    TYPE("/type"),
    CODE("/code"),
    VALUE("/value"),
    NAME("/name"),
    IDENTIFIER("/identifier"),

    // --- procedure combinations (from your screenshot) ---
    REQ_PROC_TYPE("/procedureCombination/requestedProcedureType"),
    PRE_PROC_TYPE("/procedureCombination/previousProcedureType"),

    // --- document / exemptions ---
    EXEMPTION("/documentExemptionType"),

    // --- valuation / amounts (from screenshot list) ---
    VALUATION_METHOD_TYPE("/valuationMethodType"),
    PAYABLE_AMOUNT_VALUE("/payableAmount/value"),
    PAYABLE_AMOUNT("/payableAmount"),

    SPECIFIC_BASE_VALUE("/specificBase/value"),
    SPECIFIC_BASE_UNIT("/specificBase/measureUnitType"),

    INVOICE_AMOUNT_VALUE("/invoiceValue/value"),
    INVOICE_AMOUNT("/invoiceValue"),

    AMOUNT_VALUE("/amount/value"),

    // --- commodity ---
    COMMODITY_GROSS_MASS("/commodity/grossMass"),

    // example: derived leaf using another leaf's suffix (matches screenshot style)
    COMMODITY_GROSS_MASS_VALUE(COMMODITY_GROSS_MASS.suffix + "/value"),

    // --- quota / preference ---
    QUOTA_ORDER_NUMBER("/quotaOrderNumber"),
    PREF_TYPE("/preferenceType"),

    // --- customs warehouse ---
    CUSTOMS_WAREHOUSE("/customsWarehouse"),
    CUSTOMS_WAREHOUSE_TYPE(CUSTOMS_WAREHOUSE.suffix + "/customsWarehouseType"),
    CUSTOMS_WAREHOUSE_ID(CUSTOMS_WAREHOUSE.suffix + "/warehouseID"),

    // --- trade / movement ---
    TRADE_MOVE_TYPE("/tradeMovementType"),

    // --- shorthand leaves seen in screenshot ---
    VM_TYPE("/valuationMethodType"),
    VALUATION_INDICATOR_TYPE("/valuationIndicatorType"),
    DECLARED_GUARANTEE_TYPE("/declaredGuaranteeType"),

    CATEGORY("/category"),

    METHOD_OF_PAYMENT("/methodOfPayment"),

    DECLARED_CUSTOMS_VALUE("/declaredCustomsValue/value"),

    STATISTICAL_AMOUNT_VALUE("/statisticalAmount/value"),
    STATISTICAL_AMOUNT_UNIT_TYPE("/statisticalAmount/measureUnitType");

    public final String suffix;

    Leaf(String suffix) {
        this.suffix = (suffix == null) ? "" : suffix;
    }
}
