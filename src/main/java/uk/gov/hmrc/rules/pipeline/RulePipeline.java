package uk.gov.hmrc.rules.pipeline;

import uk.gov.hmrc.rules.RuleRow;

public interface RulePipeline {
    void process(RuleRow row);
}
