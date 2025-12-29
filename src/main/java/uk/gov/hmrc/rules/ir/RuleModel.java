package uk.gov.hmrc.rules.ir;

import java.util.ArrayList;
import java.util.List;

public class RuleModel {

    private String id;
    private final List<ConditionNode> conditions = new ArrayList<>();
    private final List<ActionNode> actions = new ArrayList<>();

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
}
