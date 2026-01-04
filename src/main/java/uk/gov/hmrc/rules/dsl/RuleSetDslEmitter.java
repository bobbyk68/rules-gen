package uk.gov.hmrc.rules.dsl;

import uk.gov.hmrc.rules.ir.RuleModel;

import java.util.List;

public interface RuleSetDslEmitter {

    boolean supports(String ruleSet); // e.g. BR675

    DslEmission emit(RuleModel model);

    List<DslEntry> emitWhen(RuleModel model);

    List<DslEntry> emitThen(RuleModel model);
}
