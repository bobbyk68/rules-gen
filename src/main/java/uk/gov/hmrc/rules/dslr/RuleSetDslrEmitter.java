package uk.gov.hmrc.rules.dslr;

import uk.gov.hmrc.rules.ir.RuleModel;

public interface RuleSetDslrEmitter {
    boolean supports(String ruleSet);
    String emitDslr(RuleModel model);
}
