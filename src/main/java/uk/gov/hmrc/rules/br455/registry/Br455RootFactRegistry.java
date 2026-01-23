package uk.gov.hmrc.rules.br455.registry;

import java.util.Arrays;
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

        //String dslFieldName = dslNameFromSpreadsheet(parts);      // human-friendly
        String fieldVar = fieldVarFromBindPath(bindPath);         // $modeCode, $nationalityCode, etc.

        return new Resolved(new ResolvedField(fact, alias, bindPath));
    }

    // Version: 2026-01-23
    public ResolvedField resolve(String spreadsheetFieldPath) {
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
            default -> throw new IllegalArgumentException("Unsupported BR455 root: " + root + " in " + raw);
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

        // IMPORTANT: rewrite after routing, using fact context
        bindPath = rewriter.rewriteBindPath(fact, bindPath);

        // you compute these today but don't store them in your 3-field record (fine)
        String dslFieldName = dslNameFromSpreadsheet(segment1, parts);
        String fieldVar = fieldVarFrom(segment1, bindPath);

        return new ResolvedField(fact, alias, bindPath);
    }



    // Version: 2026-01-23
    private String toDroolsPropertyPath(String[] parts, int startIndex) {
        if (startIndex >= parts.length) {
            throw new IllegalArgumentException("No property segments after root");
        }
        StringBuilder sb = new StringBuilder(parts[startIndex]);
        for (int i = startIndex + 1; i < parts.length; i++) {
            sb.append('.').append(parts[i]);
        }
        return sb.toString();
    }

    private String dslNameFromSpreadsheet(String segment1, String[] parts) {
        // e.g. BorderTransportMeans.mode.code -> BorderTransportMeansModeCode
        if (parts.length < 2) return "Value";

        String last = parts[parts.length - 1];
        String prev = parts.length >= 2 ? parts[parts.length - 2] : "";

        String suffix;
        if (isGenericLeaf(last) && !prev.isBlank()) {
            suffix = capitalize(prev) + capitalize(last);   // ModeCode
        } else {
            suffix = capitalize(last);                      // LocationId, etc.
        }

        return capitalize(segment1) + suffix;
    }

    private String safe(String spreadsheetFieldPath) {
        return spreadsheetFieldPath == null ? "" : spreadsheetFieldPath.trim();
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

    private String fieldVarFrom(String segment1, String bindPath) {
        // Make vars stable and collision-resistant: $borderTransportMeansModeCode etc.
        if (bindPath == null || bindPath.isBlank()) return "$v";

        String[] segs = bindPath.split("\\.");
        String last = segs[segs.length - 1];
        String prev = segs.length >= 2 ? segs[segs.length - 2] : "";

        String suffix;
        if (isGenericLeaf(last) && !prev.isBlank()) {
            suffix = capitalize(prev) + capitalize(last);  // ModeCode
        } else {
            suffix = capitalize(last);                     // LocationId, etc.
        }

        return "$" + decapitalize(capitalize(segment1) + suffix);
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

    private boolean isGenericLeaf(String s) {
        return s != null && (s.equalsIgnoreCase("code") || s.equalsIgnoreCase("value") || s.equalsIgnoreCase("id"));
    }

    // Version: 2026-01-23
    public ResolvedField resolveField(String rootFact, String spreadsheetFieldPath) {

        // 1) Tokenise: e.g. "GoodsItem.investmentTypeCode" -> ["GoodsItem","investmentTypeCode"]
        // (If you already have parts elsewhere, keep that and delete this split.)
        String[] parts = spreadsheetFieldPath.split("\\.");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Empty field path");
        }

        // 2) Resolve alias for the root fact (e.g. GoodsItem -> $gi)
        // (Replace with your real alias resolver / registry call)
        String alias = aliasResolver.aliasForRoot(rootFact);

        // 3) Build an initial drools property path *after the root*.
        // If the spreadsheet includes the root in the path (common), skip it.
        int startIndex = 0;
        if (parts.length > 1 && parts[0].equals(rootFact)) {
            startIndex = 1;
        }

        String bindPath = toDroolsPropertyPath(parts, startIndex); // e.g. "investmentTypeCode"

        // 4) Rewrite exceptions / normalise to the real model path
        // e.g. "investmentTypeCode" -> "investment.typeCode"
        bindPath = fieldPathRewriter.rewriteBindPath(rootFact, bindPath);

        // 5) Freeze it: from here on, DRL generation should be dumb/string-based
        return new ResolvedField(rootFact, alias, bindPath);
    }


}
