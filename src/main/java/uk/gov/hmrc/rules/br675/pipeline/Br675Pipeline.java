package uk.gov.hmrc.rules.br675.pipeline;

import uk.gov.hmrc.rules.RuleRow;
import uk.gov.hmrc.rules.br675.dsl.Br675DslEmitter;
import uk.gov.hmrc.rules.dsl.DslEmitter;
import uk.gov.hmrc.rules.ir.RuleIrGenerator;
import uk.gov.hmrc.rules.ir.RuleModel;
import uk.gov.hmrc.rules.parsing.ConditionParser;
import uk.gov.hmrc.rules.parsing.ParsedCondition;
import uk.gov.hmrc.rules.parsing.TextConditionParser;
import uk.gov.hmrc.rules.pipeline.RulePipeline;

import java.util.List;

public final class Br675Pipeline implements RulePipeline {

    ConditionParser parser = new TextConditionParser();
    RuleIrGenerator irGen = new RuleIrGenerator();

    @Override
    public void process(RuleRow row) {
        System.out.println("=== BR675 PIPELINE ===");
        System.out.println("ruleId=" + row.id());
        System.out.println("if=" + row.ifCondition());
        System.out.println("then=" + row.thenCondition());
        System.out.println();

        ParsedCondition ifCond   = parser.parseIf(row.ifCondition());
        ParsedCondition thenCond = parser.parseThen(row.thenCondition());
        System.out.println((thenCond.toString()));

        RuleModel model = irGen.generate(row, List.of(ifCond, thenCond));
        String ruleSet = extractRuleSet(row.id());           // BR675
        DslEmitter dslEmitter = new DslEmitter(List.of(new Br675DslEmitter()));
        dslEmitter.emit(ruleSet, model);
    }

    private static String extractRuleSet(String ruleId) {
        int idx = ruleId.indexOf('_');
        return idx > 0 ? ruleId.substring(0, idx) : ruleId;
    }
}
