package uk.gov.hmrc.rules.parsing;

import uk.gov.hmrc.rules.br455.Br455ListRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Br455IfParser {

    // First dotted path like A.B.C
    private static final Pattern DOTTED_PATH = Pattern.compile("\\b[A-Za-z][A-Za-z0-9_]*(?:\\.[A-Za-z0-9_]+)+\\b");
    // First list token like #ImportCountries
    private static final Pattern LIST_TOKEN = Pattern.compile("#[A-Za-z0-9_]+");

    public Br455ListRule parse(String ifCondition) {
        String text = safe(ifCondition);

        String list = findListToken(text);
        String field = findFieldPath(text);
        Br455ListRule.Mode mode = determineMode(text);

        if (field.isBlank() || list.isBlank()) {
            throw new IllegalStateException("BR455 parse failed. fieldPath='" + field + "', listName='" + list + "', if='" + ifCondition + "'");
        }

        return new Br455ListRule(field, list, mode);
    }

    private static Br455ListRule.Mode determineMode(String text) {
        String t = safe(text).toLowerCase();

        if (t.contains("must not exist in list") ||
            t.contains("must not be in list") ||
            t.contains("must not exist")) {
            return Br455ListRule.Mode.MUST_NOT_EXIST_IN_LIST;
        }

        return Br455ListRule.Mode.MUST_EXIST_IN_LIST;
    }

    private static String findListToken(String text) {
        Matcher m = LIST_TOKEN.matcher(text);
        return m.find() ? m.group() : "";
    }

    private static String findFieldPath(String text) {
        Matcher m = DOTTED_PATH.matcher(text);
        return m.find() ? m.group() : "";
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
