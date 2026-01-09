package uk.gov.hmrc.rules.br455;

public final class Br455RootFactRegistry {

    public Resolved resolve(String spreadsheetFieldPath) {
        String raw = safe(spreadsheetFieldPath);
        if (raw.isBlank()) throw new IllegalArgumentException("BR455 fieldPath is blank");

        String[] parts = raw.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("BR455 fieldPath must include root + path: " + raw);

        String root = parts[0].trim();

        return switch (root) {
            case "Declaration" -> new Resolved("DeclarationFact", "$d", toDroolsPropertyPath(parts, 1));
            case "ConsignmentShipment" -> new Resolved("ConsignmentShipmentFact", "$cs", toDroolsPropertyPath(parts, 1));
            case "GoodsItem" -> new Resolved("GoodsItemFact", "$gi", toDroolsPropertyPath(parts, 1));
            default -> throw new IllegalArgumentException("Unsupported BR455 root: '" + root + "' in " + raw);
        };
    }

    private static String toDroolsPropertyPath(String[] parts, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) {
            String seg = parts[i].trim();
            if (seg.isEmpty()) continue;

            if (sb.length() > 0) sb.append('.');
            sb.append(toCamel(seg));
        }
        return sb.toString();
    }

    private static String toCamel(String seg) {
        if (seg.isEmpty()) return seg;

        char c0 = seg.charAt(0);
        if (Character.isLowerCase(c0)) return seg;

        return Character.toLowerCase(c0) + seg.substring(1);
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    public record Resolved(
            String factClassSimpleName, // DeclarationFact / ConsignmentShipmentFact / GoodsItemFact
            String alias,               // $d / $cs / $gi
            String propertyPath         // invoiceAmount.unitType.code etc (camelised)
    ) {}
}
