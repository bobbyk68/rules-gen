package uk.gov.hmrc.rules.ruleset;

public final class RuleSetResolver {

    private RuleSetResolver() {
    }

    public static RuleSet fromRuleId(String ruleId) {
        String id = safe(ruleId);

        if (id.startsWith("BR455_")) return RuleSet.BR_455;
        if (id.startsWith("BR675_")) return RuleSet.BR_675;

        throw new IllegalStateException("Unknown RuleSet for ruleId='" + ruleId + "'");
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
