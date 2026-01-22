package uk.gov.hmrc.rules.br455.registry.routes;

import java.util.Map;

public final class GoodsItemRoutes {

    private GoodsItemRoutes() { }

    public static Map<String, ResolutionSpec> map() {
        return Map.of(
                "additionalInformation", new ResolutionSpec("GoodsItemAdditionalDocumentFact", "$aid", 2),
                "specialProcedures",     new ResolutionSpec("GoodsItemSpecialProcedureTypeFact", "$sp",  2)
        );
    }
}
