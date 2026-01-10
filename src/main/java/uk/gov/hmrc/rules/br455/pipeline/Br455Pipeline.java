package uk.gov.hmrc.rules.br455.pipeline;

import uk.gov.hmrc.rules.br455.dsl.Br455DslEmitter;
import uk.gov.hmrc.rules.br455.dslr.Br455RuleSetDslrEmitter;
import uk.gov.hmrc.rules.dsl.DslEmitter;
import uk.gov.hmrc.rules.dsl.RuleSetDslEmitter;
import uk.gov.hmrc.rules.dslr.DslrEmitter;
import uk.gov.hmrc.rules.dslr.RuleSetDslrEmitter;
import uk.gov.hmrc.rules.pipeline.RulePipeline;

import java.util.List;
import java.util.Objects;
public final class Br455Pipeline implements RulePipeline {

    private final uk.gov.hmrc.rules.br455.parsing.Br455IfParser ifParser;
    private final uk.gov.hmrc.rules.dsl.DslEmitter dslEmitter;
    private final uk.gov.hmrc.rules.dslr.DslrEmitter dslrEmitter;

    public Br455Pipeline() {
        this(
                new uk.gov.hmrc.rules.br455.parsing.Br455IfParser(),
                new uk.gov.hmrc.rules.dsl.DslEmitter(List.of(
                        (uk.gov.hmrc.rules.dsl.RuleSetDslEmitter) new uk.gov.hmrc.rules.br455.dsl.Br455DslEmitter()
                )),
                new uk.gov.hmrc.rules.dslr.DslrEmitter(List.of(
                        (uk.gov.hmrc.rules.dslr.RuleSetDslrEmitter) new uk.gov.hmrc.rules.br455.dslr.Br455RuleSetDslrEmitter()
                ))
        );
    }

    public Br455Pipeline(
            uk.gov.hmrc.rules.br455.parsing.Br455IfParser ifParser,
            uk.gov.hmrc.rules.dsl.DslEmitter dslEmitter,
            uk.gov.hmrc.rules.dslr.DslrEmitter dslrEmitter
    ) {
        this.ifParser = java.util.Objects.requireNonNull(ifParser, "ifParser");
        this.dslEmitter = java.util.Objects.requireNonNull(dslEmitter, "dslEmitter");
        this.dslrEmitter = java.util.Objects.requireNonNull(dslrEmitter, "dslrEmitter");
    }

    @Override
    public void process(uk.gov.hmrc.rules.RuleRow row) {

        System.out.println("=== BR455 PIPELINE ===");
        System.out.println("ruleId=" + row.id());
        System.out.println("if=" + row.ifCondition());
        System.out.println("then=" + row.thenCondition());
        System.out.println();


        java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition> parsed =
                ifParser.parseParsedConditions(row.ifCondition());


        // 2) Build RuleModel with provenance
        uk.gov.hmrc.rules.ir.RuleModel model =
                new uk.gov.hmrc.rules.ir.RuleModel(row, parsed);

        // 3) Emit DSL + DSLR
        String ruleSet = row.getRuleSet().name();

        uk.gov.hmrc.rules.dsl.DslEmission dslEmission =
                dslEmitter.emit(ruleSet, model);

        String dslText = dslEmitter.renderDsl(dslEmission);
        System.out.println("DSL:\n" + dslText);

        String dslrText = dslrEmitter.emitDslr(ruleSet, model);
        System.out.println("DSLR:\n" + dslrText);
    }
}
