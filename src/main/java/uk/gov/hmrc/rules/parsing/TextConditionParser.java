package uk.gov.hmrc.rules.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Text-based parser that splits:
 *   FRONT   (quantifier)
 *   MIDDLE  (canonical path)
 *   TAIL    (operator phrase + values)
 */
public class TextConditionParser implements ConditionParser {

    private final DomainFieldResolver fieldResolver = new DomainFieldResolver();

    @Override
    public ParsedCondition parseIf(String ifText) {
        return parseClause(ifText, ConditionRole.PRIMARY);
    }

    @Override
    public ParsedCondition parseThen(String thenText) {
        return parseClause(thenText, ConditionRole.SECONDARY);
    }

    // ==========================================================
    // CHANGED METHOD: parseClause(...)
    // - Normalise "must is one of" -> "must be one of"
    // - Find operator phrases using full phrase (with spaces)
    // - Slice valuesPart using the same matched phrase length
    // ==========================================================
    private ParsedCondition parseClause(String text, ConditionRole role) {
        if (text == null) {
            throw new IllegalArgumentException("Clause text is null");
        }

        String raw = text.trim();

        // Normalise common awkward grammar variants (case-insensitive)
        // This fixes: "all X.code must is one of A,B"
        raw = raw.replaceAll("(?i)\\s+must\\s+is\\s+one\\s+of\\s+", " must be one of ");
        raw = raw.replaceAll("(?i)\\s+must\\s+is\\s+less\\s+than\\s+", " must be less than ");
        raw = raw.replaceAll("(?i)\\s+must\\s+is\\s+greater\\s+than\\s+", " must be greater than ");

        // NEW: fixes "at least one X must is provided"
        raw = raw.replaceAll("(?i)\\s+must\\s+is\\s+provided\\s*", " must be provided ");


        String lower = raw.toLowerCase();

        // 1) FRONT – quantifier
        String quantifierPhrase = null;
        if (lower.startsWith("there is at least one ")) {
            quantifierPhrase = "there is at least one ";
        } else if (lower.startsWith("at least one ")) {
            quantifierPhrase = "at least one ";
        } else if (lower.startsWith("all ")) {
            quantifierPhrase = "all ";
        }

        String afterQuantifier;
        if (quantifierPhrase != null) {
            afterQuantifier = raw.substring(quantifierPhrase.length()).trim();
        } else {
            afterQuantifier = raw;
        }

        // 2) TAIL – operator phrase
        // Keep these with leading/trailing spaces so we match cleanly between path and values.
        String[] operatorPhrases = new String[] {
                " must not be one of ",
                " must be one of ",
                " must not equal ",
                " must equals ",
                " must equal ",
                " equals ",
                " must be less than ",
                " must be greater than ",
                // NEW: allow presence checks
                " must be provided ",
                " not equals "
        };

        String lowerAfter = afterQuantifier.toLowerCase();
        int operatorIndex = -1;
        String matchedOperatorPhrase = null;

        for (String phrase : operatorPhrases) {
            String pLower = phrase.toLowerCase();
            int idx = lowerAfter.indexOf(pLower); // IMPORTANT: do NOT trim; match the full phrase
            if (idx >= 0 && (operatorIndex == -1 || idx < operatorIndex)) {
                operatorIndex = idx;
                matchedOperatorPhrase = phrase; // keep original, including spaces
            }
        }

        String canonicalPath;
        String valuesPart;
        String operatorCode;

        if (operatorIndex == -1) {
            canonicalPath = afterQuantifier;
            valuesPart    = "";
            operatorCode  = "EXISTS_AT_LEAST_ONE";
        } else {
            canonicalPath = afterQuantifier.substring(0, operatorIndex).trim();

            // slice using the exact matched phrase length (including spaces)
            valuesPart = afterQuantifier
                    .substring(operatorIndex + matchedOperatorPhrase.length())
                    .trim();

            operatorCode = mapOperatorPhraseToCode(matchedOperatorPhrase.trim());
        }

        // 3) MIDDLE – canonical path → entity/anchor/field/label
        DomainFieldDescriptor field = fieldResolver.resolve(canonicalPath);

        // 4) VALUES – often empty; in your real project codes may come from other cols
        java.util.List<String> values = parseValues(valuesPart);

        return new ParsedCondition(
                field.getEntityType(),
                field.getParentAnchorKey(),
                field.getFieldName(),
                operatorCode,
                values,
                field.getFieldTypeLabel(),
                role
        );
    }

    private String mapOperatorPhraseToCode(String phrase) {
        String p = phrase.toLowerCase().trim();

        if (p.equals("equals")) {
            return "==";
        }
        if (p.equals("must equals") || p.equals("must equal")) {
            return "==";
        }
        if (p.equals("must not equal") || p.equals("not equals")) {
            return "!=";
        }
        if (p.equals("must be one of")) {
            return "IN";
        }
        if (p.equals("must not be one of")) {
            return "NOT_IN";
        }
        if (p.equals("must be less than")) {
            return "<";
        }
        if (p.equals("must be greater")) {
            return ">";
        }
        // NEW: presence / required field
        if (p.equals("must be provided")) {
            return "IS_PROVIDED";
        }
        return "UNKNOWN_OP";
    }

    private java.util.List<String> parseValues(String valuesPart) {
        if (valuesPart == null || valuesPart.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        String cleaned = valuesPart
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .replace("'", "")
                .trim();

        if (cleaned.isEmpty()) {
            return java.util.Collections.emptyList();
        }

        String[] tokens = cleaned.split(",");
        java.util.List<String> values = new java.util.ArrayList<>();
        for (String token : tokens) {
            String v = token.trim();
            if (!v.isEmpty()) {
                values.add(v);
            }
        }
        return values;
    }
}
