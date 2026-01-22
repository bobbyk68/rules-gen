package uk.gov.hmrc.rules.br455.registry;

import java.util.Map;

public final class Br455RootFactRegistry {

    private final Map<RouteKey, RouteSpec> routes = RouteTables.routes();
    private final FieldPathRewriter rewriter = new FieldPathRewriter();

    public Resolved resolve(String spreadsheetFieldPath) {
        String raw = safe(spreadsheetFieldPath);
        if (raw.isBlank()) throw new IllegalArgumentException("BR455 fieldPath is blank");

        String[] parts = raw.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("BR455 fieldPath must include root + path: " + raw);

        String root = parts[0].trim();
        String segment1 = parts[1].trim();

        RouteSpec spec = routes.get(new RouteKey(root, segment1));

        // default: root fact
        String fact = switch (root) {
            case "Declaration" -> "DeclarationFact";
            case "ConsignmentShipment" -> "ConsignmentShipmentFact";
            case "GoodsItem" -> "GoodsItemFact";
            default -> throw new IllegalArgumentException("Unsupported BR455 root: '" + root + "' in " + raw);
        };

        String alias = switch (root) {
            case "Declaration" -> "$decl";
            case "ConsignmentShipment" -> "$cons";
            case "GoodsItem" -> "$gi";
            default -> "$root";
        };

        int startIndex = 1;

        // override: child fact
        if (spec != null) {
            fact = spec.factClassSimpleName();
            alias = spec.factAlias();
            startIndex = spec.startIndex();
        }

        String bindPath = toDroolsPropertyPath(parts, startIndex);
        bindPath = rewriter.rewriteBindPath(root, bindPath);

        String dslFieldName = dslNameFromSpreadsheet(parts);      // human-friendly
        String fieldVar = fieldVarFromBindPath(bindPath);         // $modeCode, $nationalityCode, etc.

        return new Resolved(fact, alias, bindPath, dslFieldName, fieldVar);
    }

    private String dslNameFromSpreadsheet(String[] parts) {
        // e.g. ConsignmentShipment.BorderTransportMeans.mode.code -> BorderTransportMeansModeCode
        // Use last 2-3 segments; tweak to taste.
        if (parts.length < 2) return "value";
        String leaf = parts[parts.length - 1];
        String prev = parts.length >= 2 ? parts[parts.length - 2] : "";
        if (isTooGeneric(leaf)) return prev + capitalize(leaf);
        return leaf;
    }

    private String fieldVarFromBindPath(String bindPath) {
        // e.g. mode.code -> $modeCode, nationality.code -> $nationalityCode
        if (bindPath == null || bindPath.isBlank()) return "$v";
        String[] segs = bindPath.split("\\.");
        String leaf = segs[segs.length - 1];
        if (isTooGeneric(leaf) && segs.length >= 2) {
            String prev = segs[segs.length - 2];
            return "$" + decapitalize(prev + capitalize(leaf));
        }
        return "$" + decapitalize(leaf);
    }

    private boolean isTooGeneric(String s) {
        return s != null && (s.equalsIgnoreCase("code") || s.equalsIgnoreCase("value") || s.equalsIgnoreCase("type"));
    }

    private String capitalize(String s) {
        if (s == null || s.isBlank()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String decapitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }
}
