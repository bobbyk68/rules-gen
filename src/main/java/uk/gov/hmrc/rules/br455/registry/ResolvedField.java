package uk.gov.hmrc.rules.br455.registry;

public final class ResolvedField {

    private final String rootFact;   // GoodsItem
    private final String alias;      // $gi
    private final String bindPath;   // investment.typeCode

    public ResolvedField(
            String rootFact,
            String alias,
            String bindPath
    ) {
        this.rootFact = rootFact;
        this.alias = alias;
        this.bindPath = bindPath;
    }

    public String rootFact() {
        return rootFact;
    }

    public String alias() {
        return alias;
    }

    public String bindPath() {
        return bindPath;
    }
}
