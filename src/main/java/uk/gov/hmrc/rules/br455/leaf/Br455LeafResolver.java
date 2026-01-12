package uk.gov.hmrc.rules.br455.leaf;

import uk.gov.hmrc.rules.br455.lookup.Leaf;

import java.util.Map;

public final class Br455LeafResolver {

    public enum Mode { DEMO, STRICT }

    private final Mode mode;

    private static final Map<String, Leaf> OVERRIDES = Map.of(
            // semantic exceptions
            "valuationMethod.code", Leaf.VM_TYPE,
            "previousDocuments.type.code", Leaf.CODE
    );

    public Br455LeafResolver(Mode mode) {
        this.mode = mode == null ? Mode.DEMO : mode;
    }

    public Leaf resolve(String fieldPath) {

        String canonical = canonical(fieldPath);

        // 1) explicit semantic overrides first
        Leaf override = OVERRIDES.get(canonical);
        if (override != null) return override;

        // 2) generic structural rules
        if (canonical.endsWith(".type.code")) return Leaf.CODE;
        if (canonical.endsWith(".code")) return Leaf.CODE;
        if (canonical.endsWith(".value")) return Leaf.VALUE;

        // 3) fallback
        if (mode == Mode.STRICT) {
            throw new IllegalStateException("No Leaf mapping for: " + canonical);
        }
        return Leaf.CODE;
    }

    private static String canonical(String fieldPath) {
        if (fieldPath == null) return "";
        String s = fieldPath.trim();
        if (s.startsWith("Declaration.")) return s.substring("Declaration.".length());
        if (s.startsWith("ConsignmentShipment.")) return s.substring("ConsignmentShipment.".length());
        if (s.startsWith("GoodsItem.")) return s.substring("GoodsItem.".length());
        return s;
    }
}
