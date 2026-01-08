package uk.gov.hmrc.rules.br455;

import uk.gov.hmrc.rules.RuleRow;
import uk.gov.hmrc.rules.pipeline.RulePipeline;

public final class Br455Pipeline implements RulePipeline {

    @Override
    public void process(RuleRow row) {
        System.out.println("=== BR455 PIPELINE ===");
        System.out.println("ruleId=" + row.id());
        System.out.println("if=" + row.ifCondition());
        System.out.println("then=" + row.ifCondition());
        System.out.println();
    }
}
