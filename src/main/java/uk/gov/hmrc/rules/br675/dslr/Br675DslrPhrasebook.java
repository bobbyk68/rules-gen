package uk.gov.hmrc.rules.br675.dslr;

import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public final class Br675DslrPhrasebook {

    private Br675DslrPhrasebook() {}

    // Headlines
    public static String headlineExists(String fieldTypeLabel) {
        return "Goods item with " + fieldTypeLabel + " exists";
    }

    public static String headlineNoMatchingExists(String fieldTypeLabel) {
        return "No matching goods item with " + fieldTypeLabel + " exists";
    }

    // Dash field label: special procedure + code => "special procedure code"
    // invoice amount stays "invoice amount"
    public static String dashFieldLabel(String fieldTypeLabel, String fieldName) {
        if (fieldTypeLabel == null) return safe(fieldName);

        String label = fieldTypeLabel.trim();
        // heuristic: if label already looks like the field, don’t append fieldName
        // e.g. "invoice amount" already implies invoiceAmount
        // but "special procedure" needs "code"
        if (isProbablyCompleteField(label, fieldName)) {
            return label;
        }
        return label + " " + toWords(fieldName);
    }

    private static boolean isProbablyCompleteField(String fieldTypeLabel, String fieldName) {
        if (fieldName == null || fieldName.isBlank()) return true;
        String fn = fieldName.trim().toLowerCase(Locale.ROOT);

        // common patterns where label is the “field” already
        // invoiceAmount -> "invoice amount" should remain as-is
        if (fieldTypeLabel.toLowerCase(Locale.ROOT).contains(toWords(fn))) return true;

        // if fieldName is literally "code" or "typeCode", label usually needs it
        return false;
    }

    // Dash line rendering
    public static String dashEquals(String fieldLabel, String value) {
        return "- with " + fieldLabel + " equals \"" + value + "\"";
    }

    public static String dashNotEquals(String fieldLabel, String value) {
        return "- with " + fieldLabel + " not equals \"" + value + "\"";
    }

    public static String dashIn(String fieldLabel, List<String> values) {
        return "- with " + fieldLabel + " is one of " + csvQuoted(values);
    }

    public static String dashNotIn(String fieldLabel, List<String> values) {
        return "- with " + fieldLabel + " is not one of " + csvQuoted(values);
    }

    public static String dashLessThan(String fieldLabel, String value) {
        return "- with " + fieldLabel + " is less than \"" + value + "\"";
    }

    public static String dashIsProvided(String fieldLabel) {
        return "- with " + fieldLabel + " is provided";
    }

    public static String dashIsNotProvided(String fieldLabel) {
        return "- with " + fieldLabel + " is not provided";
    }

    // Operator inversion for ALL quantifier failure mode
    public static String invertOperator(String op) {
        String u = safe(op).trim().toUpperCase(Locale.ROOT);
        return switch (u) {
            case "==" -> "!=";
            case "!=" -> "==";
            case "IN" -> "NOT_IN";
            case "NOT_IN" -> "IN";
            case "IS_PROVIDED" -> "IS_NOT_PROVIDED";
            // extend later: < -> >= etc
            default -> u;
        };
    }

    private static String csvQuoted(List<String> values) {
        StringJoiner sj = new StringJoiner(",");
        for (String v : values) {
            sj.add("\"" + v + "\"");
        }
        return sj.toString();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    // "invoiceAmount" -> "invoice amount", "typeCode" -> "type code"
    private static String toWords(String camel) {
        if (camel == null || camel.isBlank()) return "";
        StringBuilder sb = new StringBuilder();
        char[] a = camel.toCharArray();
        for (int i = 0; i < a.length; i++) {
            char c = a[i];
            if (i > 0 && Character.isUpperCase(c) && Character.isLowerCase(a[i - 1])) {
                sb.append(' ');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString().trim();
    }
}
