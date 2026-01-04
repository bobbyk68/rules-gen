package uk.gov.hmrc.rules.dsl;

public class DslWording {

    /**
     * New overload: anchor wording can now reflect EXISTS vs NOT_EXISTS.
     *
     * Target DSLR style:
     *  - EXISTS:     "Goods item with special procedure exists"
     *  - NOT_EXISTS: "No matching goods item with additional information exists"
     *
     * NOTE: still stable and still no embedded values.
     */
    public String anchorLhs(uk.gov.hmrc.rules.ir.FactConditionNode fc,
                            String quantifier,
                            String parentAnchorKey,
                            String fieldTypeLabel) {

        String anchor = norm(parentAnchorKey);
        String label = norm(fieldTypeLabel);

        boolean negated = fc != null
                && fc.getExistence() == uk.gov.hmrc.rules.ir.FactConditionNode.Existence.NOT_EXISTS;

        // BR675 main case: anchors are GI or DECL.
        if ("GI".equalsIgnoreCase(anchor)) {
            if (negated) {
                return label.isBlank()
                        ? "No matching goods item exists"
                        : "No matching goods item with " + label + " exists";
            }
            return label.isBlank()
                    ? "Goods item exists"
                    : "Goods item with " + label + " exists";
        }

        if ("DECL".equalsIgnoreCase(anchor)) {
            if (negated) {
                return label.isBlank()
                        ? "No matching declaration exists"
                        : "No matching declaration with " + label + " exists";
            }
            return label.isBlank()
                    ? "Declaration exists"
                    : "Declaration with " + label + " exists";
        }

        // Generic fallback wording
        if (negated) {
            return label.isBlank()
                    ? "No matching " + anchor + " exists"
                    : "No matching " + anchor + " with " + label + " exists";
        }
        return label.isBlank()
                ? anchor + " exists"
                : anchor + " with " + label + " exists";
    }

    /**
     * Existing method kept for compatibility. Default assumes EXISTS.
     */
    public String anchorLhs(String quantifier, String parentAnchorKey, String fieldTypeLabel) {
        String anchor = norm(parentAnchorKey);

        if ("GI".equalsIgnoreCase(anchor)) {
            return "Goods item exists";
        }
        if ("DECL".equalsIgnoreCase(anchor)) {
            return "Declaration exists";
        }

        String label = norm(fieldTypeLabel);
        if (!label.isBlank()) {
            return anchor + " " + label + " exists";
        }
        return anchor + " exists";
    }

    public String dashLhs(String field, String op) {
        return "- " + norm(field) + " " + norm(op) + " {value}";
    }

    public String emitLhs(String brCode) {
        String code = norm(brCode);
        if (code.isBlank()) {
            code = "{brCode}";
        }
        return "Then emit " + code + " {message}";
    }

    private static String norm(String s) {
        return (s == null) ? "" : s.trim();
    }
}

