package uk.gov.hmrc.rules.dslr;

import uk.gov.hmrc.rules.ir.RuleModel;

import java.util.List;

public final class DslrEmitter {

    private final List<RuleSetDslrEmitter> emitters;

    public DslrEmitter(List<RuleSetDslrEmitter> emitters) {
        this.emitters = List.copyOf(emitters);
    }

    public String emitDslr(String ruleSet, RuleModel model) {
        for (RuleSetDslrEmitter e : emitters) {
            if (e.supports(ruleSet)) {
                return e.emitDslr(model);
            }
        }
        throw new IllegalArgumentException("No DSLR emitter for ruleSet=" + ruleSet);
    }
}
