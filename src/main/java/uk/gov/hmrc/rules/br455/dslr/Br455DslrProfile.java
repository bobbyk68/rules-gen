package uk.gov.hmrc.rules.br455.dslr;

import java.util.List;
import uk.gov.hmrc.rules.br455.Br455ListRule;
import uk.gov.hmrc.rules.dslr.DslrLine;
import uk.gov.hmrc.rules.dslr.DslrWhenBlock;

import static uk.gov.hmrc.rules.br455.Br455ListRule.Mode.MUST_EXIST_IN_LIST;

// Version: 2026-01-27
public final class Br455DslrProfile {

    // =========================
    // CHANGED METHOD: buildWhenBlock
    // =========================
    // Change: previously emitted a single "dash" line.
    // Now: emits root exists + parent "provided" lines + final list predicate line.
    public DslrWhenBlock buildWhenBlock(Br455ListRule rule) {

        DslrWhenBlock block = new DslrWhenBlock();

        for (String line : buildWhenLines(rule)) {
            block.addLine(line);
        }

        return block;
    }

    public java.util.List<DslrWhenBlock> buildWhenBlocks(Br455ListRule rule) {
        return java.util.List.of(buildWhenBlock(rule));
    }

    // =========================
    // NEW METHOD: buildWhenLines
    // =========================
    private java.util.List<String> buildWhenLines(Br455ListRule rule) {

        java.util.List<String> lines = new java.util.ArrayList<>();

        String fieldPath = rule.fieldPath();
        if (fieldPath == null || fieldPath.isBlank()) {
            // Defensive: if we can't build a chain, fall back to the old single-line behaviour
            lines.add(buildFinalDashLine(rule));
            return lines;
        }

        // Split: ConsignmentShipment.DepartureTransportMeans.mode.code
        String[] parts = fieldPath.split("\\.");
        if (parts.length == 0) {
            lines.add(buildFinalDashLine(rule));
            return lines;
        }

        // 1) Root exists (no dash)
        String rootNoun = toTitleCaseWords(parts[0]); // "Consignment shipment"
        lines.add(rootNoun + " exists");

        // If there is only a root (unlikely), we just do the final line too
        if (parts.length == 1) {
            lines.add(buildFinalDashLine(rule));
            return lines;
        }

        // 2) Parent provided lines (dashed), for every segment EXCEPT the leaf
        // parents are: parts[1]..parts[n-2]
        int leafIndex = parts.length - 1;
        for (int i = 1; i <= leafIndex - 1; i++) {
            String label = buildChainLabel(parts, 1, i); // grows: "departure transport means", "... identification", "... type"
            lines.add("- with " + label + " provided");
        }

        // 3) Final leaf predicate (dashed) - reuse your existing phrasebook behaviour
        // This line should read like: "- with departure transport means identification type exist in list Deferred"
        lines.add(buildFinalDashLine(rule));


        return lines;
    }

    // Version: 2026-01-27
    private String buildFinalDashLine(Br455ListRule rule) {

        String leafLabel = humaniseLeaf(rule.fieldPath());

        return switch (rule.mode()) {
            case MUST_EXIST_IN_LIST ->
                    "- with " + leafLabel + " must exist in list " + rule.listName();
            case MUST_NOT_EXIST_IN_LIST ->
                    "- with " + leafLabel + " must not exist in list " + rule.listName();
        };
    }

    private String humaniseLeaf(String fieldPath) {
        if (fieldPath == null || fieldPath.isBlank()) {
            return "value";
        }

        String[] parts = fieldPath.split("\\.");
        if (parts.length < 2) {
            return toLowerCaseWords(parts[0]);
        }

        // everything after the root, including the leaf
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < parts.length; i++) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(toLowerCaseWords(parts[i]));
        }
        return sb.toString();
    }


    // =========================
    // NEW METHOD: buildChainLabel
    // =========================
    // Builds the human label from parts[start..end] inclusive, separated by spaces,
    // and with camel-case split to words.
    //
    // Example:
    // parts: ["ConsignmentShipment", "DepartureTransportMeans", "mode", "code"]
    // start=1,end=1 => "departure transport means"
    // start=1,end=2 => "departure transport means mode"
    private String buildChainLabel(String[] parts, int startInclusive, int endInclusive) {
        StringBuilder sb = new StringBuilder();
        for (int i = startInclusive; i <= endInclusive; i++) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(toLowerCaseWords(parts[i]));
        }
        return sb.toString();
    }

    // =========================
    // NEW METHOD: toTitleCaseWords
    // =========================
    private String toTitleCaseWords(String token) {
        String lower = toLowerCaseWords(token);
        if (lower.isBlank()) return lower;
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    // =========================
    // NEW METHOD: toLowerCaseWords
    // =========================
    // Converts "DepartureTransportMeans" -> "departure transport means"
    // Converts "mode" -> "mode"
    private String toLowerCaseWords(String token) {
        if (token == null) return "";
        String spaced = token
                .replaceAll("([a-z0-9])([A-Z])", "$1 $2")
                .replace('_', ' ')
                .trim();
        return spaced.toLowerCase(java.util.Locale.ROOT);
    }
}

