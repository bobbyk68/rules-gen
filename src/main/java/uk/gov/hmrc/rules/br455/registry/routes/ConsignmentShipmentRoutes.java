package uk.gov.hmrc.rules.br455.registry.routes;

import java.util.Map;

public final class ConsignmentShipmentRoutes {

    private ConsignmentShipmentRoutes() { }

    public static Map<String, ResolutionSpec> map() {
        return Map.of(
                // add your routes here
                // "borderTransportMeans", new ResolutionSpec("BorderTransportMeansFact", "$btm", 2)
        );
    }
}
