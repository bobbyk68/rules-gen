package uk.gov.hmrc.rules.br455.pipeline;

public class JUnitTestMethodEmitter {

    // Version: 2026-01-27

    public String emitPasteReadyJUnit5TestMethod(
            String ruleName,
            String excelIfText,
            String listName,
            java.util.List<String> listValues,
            String rootTypeSimpleName,
            java.util.List<SetterStep> steps
    ) {
        String methodName = toTestMethodName(ruleName);

        String safeRuleName = esc(ruleName);
        String safeIf = esc(normalise(excelIfText, 160));
        String safeListName = esc(listName);
        String listLiteral = toJavaListLiteral(listValues);
        String listPreview = esc(listPreview(listValues, 8));

        String displayName =
                safeRuleName
                        + " | IF: " + safeIf
                        + " | LIST " + safeListName + "=" + listPreview;

        String successBlock = emitObjectBuildBlock(rootTypeSimpleName, steps, "validValues.get(0)", "success");
        String failBlock = emitObjectBuildBlock(rootTypeSimpleName, steps, "\"NOT_IN_LIST\"", "fail");

        return """
            @DisplayName("%s")
            @Test
            void %s() {

                List<String> validValues = %s;

                // SUCCESS
            %s

                // FAIL
            %s
            }
            """.formatted(
                displayName,
                methodName,
                listLiteral,
                indent(successBlock, 8),
                indent(failBlock, 8)
        );
    }

    // -------- build blocks (setter style) --------

    private String emitObjectBuildBlock(
            String rootTypeSimpleName,
            java.util.List<SetterStep> steps,
            String leafValueExpression,
            String varPrefix
    ) {
        StringBuilder sb = new StringBuilder();

        // Root
        String rootVar = varPrefix;
        sb.append(rootTypeSimpleName).append(" ").append(rootVar)
          .append(" = new ").append(rootTypeSimpleName).append("();\n\n");

        // Parents
        // Each step: parentVar --set--> childVar, then advance to childVar
        String currentVar = rootVar;

        for (int i = 0; i < steps.size(); i++) {
            SetterStep step = steps.get(i);

            boolean isLeaf = (i == steps.size() - 1) && step.isLeaf();
            if (!isLeaf) {
                String childVar = varPrefix + (i + 1);

                sb.append(step.childTypeSimpleName()).append(" ").append(childVar)
                  .append(" = new ").append(step.childTypeSimpleName()).append("();\n");

                sb.append(currentVar).append(".").append(step.parentSetterName())
                  .append("(").append(childVar).append(");\n\n");

                currentVar = childVar;
            } else {
                // Leaf: call the leaf setter on the currentVar
                sb.append(currentVar).append(".").append(step.leafSetterName())
                  .append("(").append(leafValueExpression).append(");\n");
            }
        }

        return sb.toString().trim();
    }

    // -------- model for steps --------

    public static SetterStep parent(String childTypeSimpleName, String parentSetterName) {
        return new SetterStep(childTypeSimpleName, parentSetterName, null);
    }

    public static SetterStep leaf(String leafSetterName) {
        return new SetterStep(null, null, leafSetterName);
    }

    public record SetterStep(
            String childTypeSimpleName,
            String parentSetterName,
            String leafSetterName
    ) {
        boolean isLeaf() {
            return leafSetterName != null && !leafSetterName.isBlank();
        }
    }

    // -------- helpers --------

    private String toTestMethodName(String ruleName) {
        if (ruleName == null) return "testBR455_UNKNOWN";
        String cleaned = ruleName.replaceAll("[^A-Za-z0-9]", "");
        String number = cleaned.replaceAll(".*?(\\d+)$", "$1");
        if (number.isBlank()) number = "UNKNOWN";
        return "testBR455_" + number;
    }

    private String toJavaListLiteral(java.util.List<String> values) {
        if (values == null || values.isEmpty()) {
            return "List.of()";
        }
        String joined = values.stream()
                .map(v -> "\"" + esc(v) + "\"")
                .collect(java.util.stream.Collectors.joining(", "));
        return "List.of(" + joined + ")";
    }

    private String listPreview(java.util.List<String> values, int max) {
        if (values == null || values.isEmpty()) return "[]";
        java.util.List<String> head = values.size() <= max ? values : values.subList(0, max);
        String joined = String.join(",", head);
        return values.size() <= max ? "[" + joined + "]" : "[" + joined + ",...]";
    }

    private String normalise(String raw, int maxLen) {
        if (raw == null) return "";
        String s = raw.replaceAll("\\s+", " ").trim();
        if (s.length() <= maxLen) return s;
        return s.substring(0, Math.max(0, maxLen - 3)) + "...";
    }

    private String esc(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String indent(String s, int spaces) {
        String pad = " ".repeat(spaces);
        return java.util.Arrays.stream(s.split("\\R", -1))
                .map(line -> pad + line)
                .collect(java.util.stream.Collectors.joining("\n"));
    }
}
