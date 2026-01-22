package uk.gov.hmrc.rules.br455.registry;

import java.util.Map;

public final class FieldPathRewriter {

    private final Map<String, Map<String, String>> renamesByRoot;

    public FieldPathRewriter() {
        this.renamesByRoot = Map.of(
                "GoodsItem", Map.of(
                        "investmentType", "type"
                        // add only true exceptions here
                ),
                "ConsignmentShipment", Map.of(
                        // exceptions here
                ),
                "Declaration", Map.of(
                        // exceptions here
                )
        );
    }

    public String rewriteBindPath(String root, String bindPath) {
        if (bindPath == null || bindPath.isBlank()) return bindPath;

        Map<String, String> renames = renamesByRoot.get(root);
        if (renames == null || renames.isEmpty()) return bindPath;

        String[] segs = bindPath.split("\\.");
        for (int i = 0; i < segs.length; i++) {
            String s = segs[i];
            segs[i] = renames.getOrDefault(s, s);
        }
        return String.join(".", segs);
    }
}
