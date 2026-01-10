package uk.gov.hmrc.rules.br455.pipeline;

import uk.gov.hmrc.rules.RuleRow;
import uk.gov.hmrc.rules.br455.parsing.Br455IfParser;
import uk.gov.hmrc.rules.br455.Br455ListRule;
import uk.gov.hmrc.rules.br455.Br455ThenMessage;

public final class Br455PipelineRunner {

    private final Br455IfParser parser = new Br455IfParser();

    public void run(RuleRow row) {
        Br455ListRule parsed = parser.parse(row.ifCondition());
        String message = Br455ThenMessage.build(parsed);

        System.out.println("=== BR455 PARSED ===");
        System.out.println("ruleId   : " + row.id());
        System.out.println("fieldPath: " + parsed.fieldPath());
        System.out.println("listName : " + parsed.listName());
        System.out.println("mode     : " + parsed.mode());
        System.out.println("thenMsg  : " + message);
        System.out.println();
    }
}
