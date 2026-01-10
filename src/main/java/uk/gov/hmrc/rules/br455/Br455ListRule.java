package uk.gov.hmrc.rules.br455;

public record Br455ListRule(
        String fieldPath,
        String listName,
        Mode mode
) {
    public enum Mode {
        MUST_EXIST_IN_LIST,
        MUST_NOT_EXIST_IN_LIST
    }

    public String fieldLeaf() {
        if (fieldPath == null || fieldPath.isBlank()) return "field";
        int i = fieldPath.lastIndexOf('.');
        return i >= 0 ? fieldPath.substring(i + 1) : fieldPath;
    }

    public uk.gov.hmrc.rules.parsing.ParsedCondition toParsedCondition() {

        String path = requireNonBlank(fieldPath, "fieldPath");
        String list = requireNonBlank(listName, "listName");

        int lastDot = path.lastIndexOf('.');
        String entityType = (lastDot >= 0) ? path.substring(0, lastDot) : "ROOT";
        String fieldName = (lastDot >= 0) ? path.substring(lastDot + 1) : path;

        String operator = switch (mode) {
            case MUST_EXIST_IN_LIST -> "IN";
            case MUST_NOT_EXIST_IN_LIST -> "NOT_IN";
        };

        return new uk.gov.hmrc.rules.parsing.ParsedCondition(
                entityType,
                "ROOT", // parentAnchorKey (BR455 is root-scoped today; you can evolve later)
                fieldName,
                operator,
                java.util.List.of(list),
                fieldName, // fieldTypeLabel (keep simple; can be enriched later)
                uk.gov.hmrc.rules.parsing.ConditionRole.PRIMARY,
                uk.gov.hmrc.rules.parsing.ParsedCondition.Quantifier.AT_LEAST_ONE
        );
    }

    private static String requireNonBlank(String s, String name) {
        if (s == null || s.isBlank()) {
            throw new IllegalArgumentException(name + " is blank");
        }
        return s;
    }
}
