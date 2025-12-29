package uk.gov.hmrc.rules;

import java.util.List;

/**
 * Minimal RuleRow used in the demo.
 * You already have your own RuleRow in your real project;
 * this one is just for the standalone smoke test.
 */
public class RuleRow {

    private final String id;
    private final List<String> declarationType;
    private final List<String> procedureCategory;
    private final String ifCondition;
    private final String thenCondition;
    private final String errorCode;
    private final String param;

    public RuleRow(String id,
                   List<String> declarationType,
                   List<String> procedureCategory,
                   String ifCondition,
                   String thenCondition,
                   String errorCode,
                   String param) {
        this.id = id;
        this.declarationType = List.copyOf(declarationType);
        this.procedureCategory = List.copyOf(procedureCategory);
        this.ifCondition = ifCondition;
        this.thenCondition = thenCondition;
        this.errorCode = errorCode;
        this.param = param;
    }

    public String id() {
        return id;
    }

    public List<String> declarationType() {
        return declarationType;
    }

    public List<String> procedureCategory() {
        return procedureCategory;
    }

    public String ifCondition() {
        return ifCondition;
    }

    public String thenCondition() {
        return thenCondition;
    }

    public String errorCode() {
        return errorCode;
    }

    public String param() {
        return param;
    }
}
