package uk.gov.hmrc.rules.pipeline;

import java.util.EnumMap;
import java.util.Map;
import uk.gov.hmrc.rules.br455.Br455Pipeline;
import uk.gov.hmrc.rules.br675.Br675Pipeline;
import uk.gov.hmrc.rules.ruleset.RuleSet;

public final class RulePipelineRegistry {

    private final Map<RuleSet, RulePipeline> byRuleSet = new EnumMap<>(RuleSet.class);

    public RulePipelineRegistry() {
        byRuleSet.put(RuleSet.BR455, new Br455Pipeline());
        byRuleSet.put(RuleSet.BR675, new Br675Pipeline());
    }

    public RulePipeline get(RuleSet ruleSet) {
        RulePipeline p = byRuleSet.get(ruleSet);
        if (p == null) throw new IllegalStateException("No pipeline registered for " + ruleSet);
        return p;
    }
}
