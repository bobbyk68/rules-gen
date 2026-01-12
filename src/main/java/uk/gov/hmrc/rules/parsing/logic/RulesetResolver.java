package uk.gov.hmrc.rules.parsing.logic;

public final class RulesetResolver {

    private RulesetResolver() { }

    public static String resolveRuleset(String ruleId) {
        if (ruleId == null) {
            return "";
        }
        String trimmed = ruleId.trim();
        int idx = trimmed.indexOf('_');
        if (idx <= 0) {
            return trimmed; // fallback
        }
        return trimmed.substring(0, idx);
    }
}
