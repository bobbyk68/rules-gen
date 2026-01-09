package uk.gov.hmrc.rules.dslr;

import java.util.List;
import uk.gov.hmrc.rules.br455.Br455IfParser;
import uk.gov.hmrc.rules.br455.Br455ListRule;
import uk.gov.hmrc.rules.emitter.Br455DslrProfile;
import uk.gov.hmrc.rules.emitter.DslrWhenBlock;
import uk.gov.hmrc.rules.ir.RuleModel;
import uk.gov.hmrc.rules.RuleRow;

public final class Br455RuleSetDslrEmitter implements RuleSetDslrEmitter {

    private final Br455IfParser parser = new Br455IfParser();
    private final Br455DslrProfile profile = new Br455DslrProfile();

    @Override
    public boolean supports(String ruleSet) {
        return ruleSet != null && ruleSet.trim().equalsIgnoreCase("BR455");
    }

    @Override
    public String emitDslr(RuleModel model) {

        RuleRow row = model.ruleRow();
        String ruleId = row.id();
        String ifCondition = row.ifCondition();

        Br455ListRule rule = parser.parse(ifCondition);

        List<DslrWhenBlock> whenBlocks = profile.buildWhenBlocks(rule);

        StringBuilder sb = new StringBuilder();
        sb.append("rule \"").append(ruleId).append("\"\n");
        sb.append("when\n");

        for (DslrWhenBlock b : whenBlocks) {

            // IMPORTANT: your DslrWhenBlock is not using getX() style
            sb.append("    ").append(b.headline()).append("\n");

            for (var line : b.lines()) {
                sb.append("    ").append(line.toString()).append("\n");
            }

        }

        sb.append("then\n");
        sb.append("    ").append(emitThenPrintln(rule)).append("\n");
        sb.append("end\n");


        return sb.toString();
    }



    private String emitThenPrintln(Br455ListRule rule) {
        String field = rule.fieldPath(); // invoiceAmount.unitType.code
        String list = rule.listName();

        String msg = switch (rule.mode()) {
            case MUST_EXIST_IN_LIST -> field + " does not exist in list " + list;
            case MUST_NOT_EXIST_IN_LIST -> field + " does exist in list " + list;
        };

        return "Emit 455 (\"" + msg + "\");";
    }

}
