package uk.gov.hmrc.rules.br455.registry;

public final class Br455RootFactRegistry {

    public Resolved resolve(String spreadsheetFieldPath) {
        String raw = safe(spreadsheetFieldPath);
        if (raw.isBlank()) throw new IllegalArgumentException("BR455 fieldPath is blank");

        String[] parts = raw.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("BR455 fieldPath must include root + path: " + raw);

        String root = parts[0].trim();

        return switch (root) {

            case "Declaration" ->
                    new Resolved("DeclarationFact", "$decl", toDroolsPropertyPath(parts, 1));

            case "ConsignmentShipment" ->
                    new Resolved("ConsignmentShipmentFact", "$cons", toDroolsPropertyPath(parts, 1));

            case "GoodsItem" -> resolveGoodsItem(parts, raw);

            default ->
                    throw new IllegalArgumentException("Unsupported BR455 root: '" + root + "' in " + raw);
        };
    }

    private Resolved resolveGoodsItem(String[] parts, String raw) {

        // parts[0] = GoodsItem
        // parts[1] = first segment after root (e.g. "additionalInformation", "specialProcedures", ...)
        String segment1 = parts.length > 1 ? parts[1].trim() : "";

        // --- Path-aware overrides (specific beats general) -----------------------

        if ("additionalInformation".equals(segment1)) {
            // GoodsItem.additionalInformation.code -> GoodsItemAdditionalDocumentFact(code)
            // Typically, for a child fact, the property path should start *within that child*.
            // So "code" not "additionalInformation.code"
            if (parts.length < 3) {
                throw new IllegalArgumentException("BR455 GoodsItem.additionalInformation must include a field: " + raw);
            }
            return new Resolved(
                    "GoodsItemAdditionalDocumentFact",
                    "$aid",
                    toDroolsPropertyPath(parts, 2)
            );
        }

        if ("specialProcedures".equals(segment1)) {
            if (parts.length < 3) {
                throw new IllegalArgumentException("BR455 GoodsItem.specialProcedures must include a field: " + raw);
            }
            return new Resolved(
                    "GoodsItemSpecialProcedureTypeFact",
                    "$sp",
                    toDroolsPropertyPath(parts, 2)
            );
        }

        // --- Default GoodsItem fallback ------------------------------------------
        return new Resolved("GoodsItemFact", "$gi", toDroolsPropertyPath(parts, 1));
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
