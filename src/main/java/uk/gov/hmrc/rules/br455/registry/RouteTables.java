package uk.gov.hmrc.rules.br455.registry;

import java.util.HashMap;
import java.util.Map;

public final class RouteTables {

    private RouteTables() { }

    public static Map<RouteKey, RouteSpec> routes() {
        Map<RouteKey, RouteSpec> m = new HashMap<>();

        // ConsignmentShipment child groups
        m.put(new RouteKey("ConsignmentShipment", "BorderTransportMeans"),
                new RouteSpec("BorderTransportMeansFact", "$btm", 2));

        m.put(new RouteKey("ConsignmentShipment", "ArrivalTransportMeans"),
                new RouteSpec("ArrivalTransportMeansFact", "$atm", 2));

        m.put(new RouteKey("ConsignmentShipment", "previousDocuments"),
                new RouteSpec("PreviousDocumentFact", "$prevDoc", 2));

        m.put(new RouteKey("ConsignmentShipment", "GoodsLocation"),
                new RouteSpec("GoodsLocationFact", "$glc", 2));

        // GoodsItem child groups
        m.put(new RouteKey("GoodsItem", "additionalInformation"),
                new RouteSpec("GoodsItemAdditionalInformationFact", "$ai", 2));

        m.put(new RouteKey("GoodsItem", "additionalDocuments"),
                new RouteSpec("GoodsItemAdditionalDocumentFact", "$ad", 2));

        m.put(new RouteKey("GoodsItem", "valuationAdjustments"),
                new RouteSpec("GoodsItemValuationAdjustmentFact", "$va", 2));

        // Declaration child groups (if any)
        m.put(new RouteKey("Declaration", "AuthorizationHolder"),
                new RouteSpec("AuthorizationHolderFact", "$auth", 2));

        return Map.copyOf(m);
    }
}
