package uk.gov.hmrc.rules.ir;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParentConditionNode extends ConditionNode {

    private String alias;
    private String factType;
    private final Map<String, Constraint> fieldConstraints = new LinkedHashMap<>();

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFactType() {
        return factType;
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    public Map<String, Constraint> getFieldConstraints() {
        return fieldConstraints;
    }
}
