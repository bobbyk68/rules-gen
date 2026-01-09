package uk.gov.hmrc.rules.dsl;

import uk.gov.hmrc.rules.ir.RuleModel;

import java.util.List;

public class DslEmitter {

    private final List<RuleSetDslEmitter> emitters;

    public DslEmitter(List<RuleSetDslEmitter> emitters) {
        this.emitters = List.copyOf(emitters);
    }

    public DslEmission emit(String ruleSet, RuleModel model) {
        for (RuleSetDslEmitter e : emitters) {
            if (e.supports(ruleSet)) {
                return e.emit(model);
            }
        }
        throw new IllegalArgumentException("No DSL emitter for ruleSet=" + ruleSet);
    }

    public String renderDsl(DslEmission emission) {

        StringBuilder sb = new StringBuilder();

        sb.append("[when]\n");
        for (DslEntry e : emission.whenEntries()) {
            sb.append(e.getLhs()).append(" = ").append(e.getRhs()).append("\n");
        }

        sb.append("\n[then]\n");
        for (DslEntry e : emission.thenEntries()) {
            sb.append(e.getLhs()).append(" = ").append(e.getRhs()).append("\n");
        }

        return sb.toString();
    }
}
