package uk.gov.hmrc.rules.br455.parsing;

import uk.gov.hmrc.rules.br455.Br455ListRule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Br455IfParser {

    private static final Pattern DOTTED_PATH =
            Pattern.compile("\\b[A-Za-z][A-Za-z0-9_]*(?:\\.[A-Za-z0-9_]+)+\\b");

    private static final Pattern HASH_LIST =
            Pattern.compile("#[A-Za-z0-9_]+");

    private static final Pattern LIST_WORD =
            Pattern.compile("(?i)\\blist\\s+([A-Za-z0-9_]+)\\b");

    public java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition> parseParsedConditions(String ifText) {
        uk.gov.hmrc.rules.br455.Br455ListRule rule = parse(ifText); // your existing method
        return java.util.List.of(rule.toParsedCondition());
    }


    public Br455ListRule parse(String ifCondition) {
        String text = safe(ifCondition);

        String field = findFieldPath(text);
        String list = findListName(text);
        Br455ListRule.Mode mode = determineMode(text);

        if (field.isBlank() || list.isBlank()) {
            throw new IllegalStateException(
                    "BR455 parse failed. fieldPath='" + field + "', listName='" + list + "', if='" + ifCondition + "'"
            );
        }

        return new Br455ListRule(field, list, mode);
    }

    private static Br455ListRule.Mode determineMode(String text) {
        String t = safe(text).toLowerCase();

        if (t.contains("must not exist in list")
                || t.contains("must not be in list")
                || t.contains("must not exist")) {
            return Br455ListRule.Mode.MUST_NOT_EXIST_IN_LIST;
        }
        return Br455ListRule.Mode.MUST_EXIST_IN_LIST;
    }

    private static String findFieldPath(String text) {
        Matcher m = DOTTED_PATH.matcher(text);
        return m.find() ? m.group() : "";
    }

    private static String findListName(String text) {

        Matcher h = HASH_LIST.matcher(text);
        if (h.find()) {
            String token = h.group();
            return token.startsWith("#") ? token.substring(1) : token;
        }

        Matcher w = LIST_WORD.matcher(text);
        if (w.find()) {
            return w.group(1);
        }

        return "";
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
