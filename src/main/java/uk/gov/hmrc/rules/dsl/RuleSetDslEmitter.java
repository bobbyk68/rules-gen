package uk.gov.hmrc.rules.dsl;

import uk.gov.hmrc.rules.ir.RuleModel;

import java.util.List;

public interface RuleSetDslEmitter {

    boolean supports(String ruleSet); // e.g. BR675

    DslEmission emit(RuleModel model);

    java.util.List<DslEntry> emitWhen(RuleModel model);

    java.util.List<DslEntry> emitThen(RuleModel model);
}
