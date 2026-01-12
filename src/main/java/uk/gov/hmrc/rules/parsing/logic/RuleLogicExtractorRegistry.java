package uk.gov.hmrc.rules.parsing.logic;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class RuleLogicExtractorRegistry {

    private final Map<String, RuleLogicExtractor> byRuleset = new HashMap<>();
    private final RuleLogicExtractor fallback;

    public RuleLogicExtractorRegistry(RuleLogicExtractor fallback) {
        this.fallback = Objects.requireNonNull(fallback, "fallback");
    }

    public RuleLogicExtractorRegistry register(String ruleset, RuleLogicExtractor extractor) {
        if (ruleset == null || ruleset.trim().isEmpty()) {
            throw new IllegalArgumentException("ruleset must not be blank");
        }
        byRuleset.put(ruleset.trim(), Objects.requireNonNull(extractor, "extractor"));
        return this;
    }

    public RuleLogicExtractor forRuleset(String ruleset) {
        if (ruleset == null) {
            return fallback;
        }
        RuleLogicExtractor extractor = byRuleset.get(ruleset.trim());
        return extractor != null ? extractor : fallback;
    }
}
