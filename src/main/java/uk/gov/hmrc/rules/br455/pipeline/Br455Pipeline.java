package uk.gov.hmrc.rules.br455.pipeline;

import uk.gov.hmrc.rules.br455.dsl.Br455DslEmitter;
import uk.gov.hmrc.rules.br455.dslr.Br455RuleSetDslrEmitter;
import uk.gov.hmrc.rules.br455.parsing.Br455IfParser;
import uk.gov.hmrc.rules.dsl.DslEmission;
import uk.gov.hmrc.rules.dsl.DslEmitter;
import uk.gov.hmrc.rules.dslr.DslrEmitter;
import uk.gov.hmrc.rules.ir.RuleModel;
import uk.gov.hmrc.rules.parsing.ParsedCondition;
import uk.gov.hmrc.rules.pipeline.RulePipeline;

import java.util.List;
import java.util.Objects;

public final class Br455Pipeline implements RulePipeline {

    private final Br455IfParser ifParser;
    private final DslEmitter dslEmitter;
    private final DslrEmitter dslrEmitter;

    public Br455Pipeline() {
        this(
                new Br455IfParser(),
                new DslEmitter(List.of(new Br455DslEmitter())),
                new DslrEmitter(List.of(new Br455RuleSetDslrEmitter()))
        );
    }

    public Br455Pipeline(
            Br455IfParser ifParser,
            DslEmitter dslEmitter,
            DslrEmitter dslrEmitter
    ) {
        this.ifParser = Objects.requireNonNull(ifParser, "ifParser");
        this.dslEmitter = Objects.requireNonNull(dslEmitter, "dslEmitter");
        this.dslrEmitter = Objects.requireNonNull(dslrEmitter, "dslrEmitter");
    }

    @Override
    public void process(uk.gov.hmrc.rules.RuleRow row) {

        System.out.println("=== BR455 PIPELINE ===");
        System.out.println("ruleId=" + row.id());
        System.out.println("if=" + row.ifCondition());
        System.out.println("then=" + row.thenCondition());
        System.out.println();

        List<ParsedCondition> parsed = ifParser.parseParsedConditions(row.ifCondition());

        RuleModel model = new RuleModel(row, parsed);
        String ruleSet = row.getRuleSet().name();
        DslEmission dslEmission = dslEmitter.emit(ruleSet, model);

        String dslText = dslEmitter.renderDsl(dslEmission);
        System.out.println("DSL:\n" + dslText);
        String dslrText = dslrEmitter.emitDslr(ruleSet, model);
        System.out.println("DSLR:\n" + dslrText);
    }
}
