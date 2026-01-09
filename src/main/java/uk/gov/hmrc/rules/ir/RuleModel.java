package uk.gov.hmrc.rules.ir;

import uk.gov.hmrc.rules.parsing.ParsedCondition;

import java.util.ArrayList;
import java.util.List;

public class RuleModel {

    private String id;
    private EmissionScope scope;
    private final List<ConditionNode> conditions = new ArrayList<>();
    private final List<ActionNode> actions = new ArrayList<>();
    private final uk.gov.hmrc.rules.RuleRow ruleRow;


    public EmissionScope getScope() { return scope; }
    public void setScope(EmissionScope scope) { this.scope = scope; }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<ConditionNode> getConditions() {
        return conditions;
    }
    public List<ActionNode> getActions() {
        return actions;
    }


    // Whatever you already have as the compiled representation / IR root:
    // Keep your existing fields exactly as they are.
    // Example placeholders below — replace with your real fields.
    private final java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition> parsedConditions;

    public RuleModel(
            uk.gov.hmrc.rules.RuleRow ruleRow,
            java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition> parsedConditions
    ) {
        this.ruleRow = java.util.Objects.requireNonNull(ruleRow, "ruleRow");
        this.parsedConditions = java.util.List.copyOf(parsedConditions);
    }

    /**
     * Provenance: the original Excel-like input row.
     * For demo & debugging, emitters may read id / ifCondition / thenCondition from here.
     */
    public uk.gov.hmrc.rules.RuleRow ruleRow() {
        return ruleRow;
    }

    public List<ParsedCondition> parsedConditions() {
        return parsedConditions;
    }

}
