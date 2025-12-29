package uk.gov.hmrc.rules.ir;

import java.util.LinkedHashMap;
import java.util.Map;

public class FactConditionNode extends ConditionNode {

    private String alias;
    private String factType;
    private String parentAlias;
    private String parentField = "seq";
    private String parentJoinField = "parentSeq";

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

    public String getParentAlias() {
        return parentAlias;
    }

    public void setParentAlias(String parentAlias) {
        this.parentAlias = parentAlias;
    }

    public String getParentField() {
        return parentField;
    }

    public void setParentField(String parentField) {
        this.parentField = parentField;
    }

    public String getParentJoinField() {
        return parentJoinField;
    }

    public void setParentJoinField(String parentJoinField) {
        this.parentJoinField = parentJoinField;
    }

    public Map<String, Constraint> getFieldConstraints() {
        return fieldConstraints;
    }
}
