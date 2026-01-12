package uk.gov.hmrc.rules.parsing.logic.impl;

import uk.gov.hmrc.rules.parsing.logic.LogicExtractionResult;
import uk.gov.hmrc.rules.parsing.logic.RuleLogicExtractor;

public final class Br455LogicExtractor implements RuleLogicExtractor {

    @Override
    public LogicExtractionResult extract(String rawCol6) {
        String raw = normalize(rawCol6);

        // Split into lines (many BR455 cells have condition + emit message on another line)
        String[] lines = raw.isEmpty() ? new String[0] : raw.split("\n");

        String condition = "";
        String message = "";

        if (lines.length == 0) {
            return LogicExtractionResult.of("", "", "");
        }

        // Heuristic:
        // - First non-empty line is the condition
        // - A line starting with "emit " (case-insensitive) is the message
        for (String line : lines) {
            String t = line == null ? "" : line.trim();
            if (t.isEmpty()) continue;

            if (condition.isEmpty()) {
                condition = t;
                continue;
            }

            if (startsWithIgnoreCase(t, "emit ")) {
                message = t;
            }
        }

        // If no explicit emit line was found, you can derive a message from the condition if desired.
        // For now, keep it simple:
        String cleanedCondition = stripTrailingElseInvalid(condition);

        // For BR455, thenText is typically not "logic"; it’s message/outcome.
        return LogicExtractionResult.of(cleanedCondition, message, message);
    }

    private static String stripTrailingElseInvalid(String condition) {
        if (condition == null) return "";
        String t = condition.trim();
        // Remove "Else invalid" suffix (case-insensitive), if present
        String upper = t.toUpperCase();
        String marker = " ELSE INVALID";
        int idx = upper.lastIndexOf(marker);
        if (idx >= 0 && idx == upper.length() - marker.length()) {
            return t.substring(0, idx).trim();
        }
        return t;
    }

    private static boolean startsWithIgnoreCase(String s, String prefix) {
        if (s == null || prefix == null) return false;
        if (s.length() < prefix.length()) return false;
        return s.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    private static String normalize(String s) {
        if (s == null) return "";
        return s.replace("\r\n", "\n").trim();
    }
}
