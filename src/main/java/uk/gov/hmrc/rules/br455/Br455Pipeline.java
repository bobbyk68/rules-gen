package uk.gov.hmrc.rules.br455;

import uk.gov.hmrc.rules.RuleRow;
import uk.gov.hmrc.rules.dsl.DslEmitter;
import uk.gov.hmrc.rules.dsl.RuleSetDslEmitter;
import uk.gov.hmrc.rules.dslr.DslrEmitter;
import uk.gov.hmrc.rules.dslr.RuleSetDslrEmitter;
import uk.gov.hmrc.rules.pipeline.RulePipeline;

import java.util.List;
import java.util.Objects;

public final class Br455Pipeline implements RulePipeline {

    private final DslEmitter dslEmitter;
    private final DslrEmitter dslrEmitter;

    public Br455Pipeline() {
        this(
                new DslEmitter(List.of(
                        (RuleSetDslEmitter) new uk.gov.hmrc.rules.dsl.Br455RuleSetDslEmitter()
                )),
                new DslrEmitter(List.of(
                        (RuleSetDslrEmitter) new uk.gov.hmrc.rules.dslr.Br455RuleSetDslrEmitter()
                ))
        );
    }

    public Br455Pipeline(DslEmitter dslEmitter, DslrEmitter dslrEmitter) {
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

        // 1) Parse -> ParsedConditions (even if BR455 only yields one)
        java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition> parsed =
               List.of();

        // 2) Build RuleModel with provenance
        uk.gov.hmrc.rules.ir.RuleModel model =
                new uk.gov.hmrc.rules.ir.RuleModel(row, parsed);

        // 3) Emit DSL
        String ruleSet = row.getRuleSet().name(); // or row.getRuleSet().name() if it's an enum
        uk.gov.hmrc.rules.dsl.DslEmission dslEmission =
                dslEmitter.emit(ruleSet, model);

        String dslText = dslEmitter.renderDsl(dslEmission);

        System.out.println("DSL:\n" + dslText);

        // 4) Emit DSLR
        String dslrText = dslrEmitter.emitDslr(ruleSet, model);

        System.out.println("DSLR:\n" + dslrText);
    }
}
