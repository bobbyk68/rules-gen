package uk.gov.hmrc.rules.br455.lookup;

import uk.gov.hmrc.rules.br455.leaf.Br455Leaf;

public final class Br455LeafResolver {

    public enum Mode { DEMO, STRICT }

    private final Mode mode;

    private static final java.util.Map<String, Br455Leaf> OVERRIDES = java.util.Map.of(
            "valuationMethod.code", Br455Leaf.VM_TYPE,
            "previousDocuments.type.code", Br455Leaf.TYPE_CODE
    );

    public Br455LeafResolver(Mode mode) {
        this.mode = (mode == null) ? Mode.DEMO : mode;
    }

    public Br455Leaf resolve(String fieldPath) {

        String canonical = canonical(fieldPath);

        Br455Leaf override = OVERRIDES.get(canonical);
        if (override != null) return override;

        if (canonical.endsWith(".type.code")) return Br455Leaf.TYPE_CODE;
        if (canonical.endsWith(".code")) return Br455Leaf.CODE;
        if (canonical.endsWith(".value")) return Br455Leaf.VALUE;

        if (mode == Mode.STRICT) {
            throw new IllegalStateException("No Br455Leaf mapping for: " + canonical);
        }
        return Br455Leaf.CODE;
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
