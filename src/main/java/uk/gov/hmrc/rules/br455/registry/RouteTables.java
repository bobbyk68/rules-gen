package uk.gov.hmrc.rules.br455.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RouteTables {


        private static final Map<RouteKey, RouteSpec> ROUTES = build();

        private static Map<RouteKey, RouteSpec> build() {
            Map<RouteKey, RouteSpec> m = new HashMap<>();

            // 🔽 your 13 entries go here (exactly as shown)

            return Map.copyOf(m);
        }

        public static Optional<RouteSpec> lookup(RouteKey key) {
            return Optional.ofNullable(ROUTES.get(key));
        }

    public static Map<RouteKey, RouteSpec> routes() {
            return build();
    }
}
