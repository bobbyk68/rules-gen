package uk.gov.hmrc.rules.br455.resolve;

// Version: 2026-01-24
public final class SpreadsheetPathParser {

    private SpreadsheetPathParser() {
        // utility
    }

    public static String[] splitSpreadsheetPath(String spreadsheetFieldPath) {

        if (spreadsheetFieldPath == null) {
            throw new IllegalArgumentException("spreadsheetFieldPath must not be null");
        }

        String s = spreadsheetFieldPath.trim();
        if (s.isEmpty()) {
            throw new IllegalArgumentException("spreadsheetFieldPath must not be blank");
        }

        // Remove index markers like [1]
        s = s.replaceAll("\\[\\s*\\d+\\s*\\]", "");

        // Normalise arrows to dots
        s = s.replace("→", "->");
        s = s.replaceAll("\\s*->\\s*", ".");

        // Split on ".", "/", "\"
        String[] raw = s.split("[./\\\\]+");

        java.util.ArrayList<String> parts = new java.util.ArrayList<>();
        for (String seg : raw) {
            if (seg == null) {
                continue;
            }
            String t = seg.trim();
            if (!t.isEmpty()) {
                parts.add(t);
            }
        }

        if (parts.isEmpty()) {
            throw new IllegalArgumentException("No path segments could be extracted from: " + spreadsheetFieldPath);
        }

        return parts.toArray(new String[0]);
    }
}
