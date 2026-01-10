package uk.gov.hmrc.rules.br455.format;

import uk.gov.hmrc.rules.br455.Br455ListRule;

public final class Br455ThenMessageFormatter {

    private Br455ThenMessageFormatter() {
        // utility
    }

    public static String friendlyPathNoDotsx(String fullFieldPath) {
        // fullFieldPath examples:
        //  - "Declaration.invoiceAmount.unitType.code"
        //  - "ConsignmentShipment.BorderTransportMeans.mode.code"
        //  - "GoodsItem.previousDocuments.category.code"
        String stripped = stripRoot(fullFieldPath);

        // Remove dots => spaces (friendlier)
        // invoiceAmount.unitType.code -> invoiceAmount unitType code
        // BorderTransportMeans.mode.code -> BorderTransportMeans mode code
        return stripped.replace('.', ' ').trim();
    }

    public static String friendlyPathNoDots(String fieldPath) {
        if (fieldPath == null) return "";
        return fieldPath.trim().replace('.', ' ');
    }


    // =========================
// NEW METHOD: buildWhenDslLhs
// =========================
    public static String buildWhenDslLhs(String fieldPath, Br455ListRule.Mode mode) {

        // Reuse your existing friendlyPathNoDots logic if you have it,
        // otherwise do the simple dot -> space conversion.
        String pathNoDots = friendlyPathNoDots(fieldPath);

        String phrase = (mode == Br455ListRule.Mode.MUST_EXIST_IN_LIST)
                ? "must exist in list {value}"
                : "must not exist in list {value}";

        return "- " + pathNoDots + " " + phrase;
    }

//    public static String buildWhenDslLhs(
//            String fieldPath,
//            Br455ListRule.Mode mode
//    ) {
//        String path = friendlyPathNoDots(fieldPath);
//
//        String phrase = (mode == Br455ListRule.Mode.MUST_EXIST_IN_LIST)
//                ? "must exist in list {value}"
//                : "must not exist in list {value}";
//
//        return path + " " + phrase;
//    }

    public static String existPhrase(Br455ListRule.Mode mode) {
        return (mode == Br455ListRule.Mode.MUST_EXIST_IN_LIST)
                ? "must exist in list"
                : "must not exist in list";
    }

    /**
     * This is the full human sentence we want in BOTH:
     * - DSL then LHS (human key)
     * - RHS string printed/logged
     */
    public static String buildThenSentence(String ruleSet, String fullFieldPath, Br455ListRule.Mode mode, String listName) {
        String pathNoDots = friendlyPathNoDots(fullFieldPath);
        String phrase = existPhrase(mode);

        // Example:
        // Emit BR455 validation error for invoiceAmount unitType code must exist in list CurrencyTypes
        return "Emit " + ruleSet + " validation error for " + pathNoDots + " " + phrase + " " + listName;
    }

    // Kept here so the formatter is self-contained and re-usable across emitters
    private static String stripRoot(String fieldPath) {
        if (fieldPath == null) return "";
        String s = fieldPath.trim();
        if (s.startsWith("Declaration.")) return s.substring("Declaration.".length());
        if (s.startsWith("ConsignmentShipment.")) return s.substring("ConsignmentShipment.".length());
        if (s.startsWith("GoodsItem.")) return s.substring("GoodsItem.".length());
        return s;
    }


    public static String buildThenDslLhs(String ruleSet, String fieldPath, Br455ListRule.Mode mode, String listName) {
        return "Then emit " + ruleSet + " validation error for " + friendlyField(fieldPath, mode, listName);
    }

    public static String buildThenMessage(String ruleSet, String fieldPath, Br455ListRule.Mode mode, String listName) {
        return "Emit " + ruleSet + " validation error for " + friendlyField(fieldPath, mode, listName);
    }

    private static String friendlyField(String fieldPath, Br455ListRule.Mode mode, String listName) {
        String tail = stripRoot(fieldPath);

        // remove dots for readability: "invoiceAmount.unitType.code" -> "invoiceAmount unitType code"
        String noDots = tail.replace('.', ' ').trim().replaceAll("\\s+", " ");

        String verb = (mode == Br455ListRule.Mode.MUST_EXIST_IN_LIST)
                ? "must exist in list "
                : "must not exist in list ";

        return noDots + " " + verb + listName;
    }


}

