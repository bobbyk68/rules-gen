package uk.gov.hmrc.rules.parsing.logic.impl;

import uk.gov.hmrc.rules.parsing.logic.LogicExtractionResult;
import uk.gov.hmrc.rules.parsing.logic.RuleLogicExtractor;

public final class Br675LogicExtractor implements RuleLogicExtractor {

    @Override
    public LogicExtractionResult extract(String rawCol6) {
        String raw = normalize(rawCol6);

        // Common case: two lines (or more) with "IF" and "THEN" markers.
        // We'll find the first THEN and split there.
        int thenIdx = indexOfThen(raw);
        if (thenIdx >= 0) {
            String before = raw.substring(0, thenIdx).trim();
            String after = raw.substring(thenIdx).trim();

            String ifText = stripLeadingKeyword(before, "IF");
            String thenText = stripLeadingKeyword(after, "THEN");

            // For 675 you may also want messageText to come from thenText later
            return LogicExtractionResult.of(ifText, thenText, thenText);
        }

        // No THEN found: treat entire cell as IF
        String ifText = stripLeadingKeyword(raw, "IF");
        return LogicExtractionResult.of(ifText, "", "");
    }

    private static int indexOfThen(String raw) {
        if (raw == null || raw.isEmpty()) return -1;

        // Look for THEN as a word boundary, case-insensitive
        String upper = raw.toUpperCase();
        int idx = upper.indexOf("\nTHEN");
        if (idx >= 0) return idx + 1; // split at 'THEN' start (after newline)

        // single-line " THEN "
        idx = upper.indexOf(" THEN ");
        if (idx >= 0) return idx + 1;

        // line starts with THEN
        idx = upper.indexOf("THEN ");
        if (idx == 0) return 0;

        return -1;
    }

    private static String stripLeadingKeyword(String s, String keyword) {
        if (s == null) return "";
        String t = s.trim();
        String k = keyword.toUpperCase() + " ";
        if (t.toUpperCase().startsWith(k)) {
            return t.substring(k.length()).trim();
        }
        // Sometimes "IF:" / "THEN:" appear
        String k2 = keyword.toUpperCase() + ":";
        if (t.toUpperCase().startsWith(k2)) {
            return t.substring(k2.length()).trim();
        }
        return t;
    }

    private static String normalize(String s) {
        if (s == null) return "";
        // Keep newlines but normalise \r\n
        return s.replace("\r\n", "\n").trim();
    }
}
