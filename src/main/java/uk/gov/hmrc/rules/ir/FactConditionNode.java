package uk.gov.hmrc.rules.ir;

import java.util.LinkedHashMap;
import java.util.Map;

public class FactConditionNode extends ConditionNode {

    public enum Existence {
        EXISTS,
        NOT_EXISTS
    }

    private String alias;
    private String factType;
    private String parentAlias;
    private String parentField = "seq";
    private String parentJoinField = "parentSeq";

    /**
     * Key semantic needed for BR675 DSLR:
     *  - EXISTS: "Goods item with special procedure exists"
     *  - NOT_EXISTS: "No matching goods item with additional information exists"
     */
    private Existence existence = Existence.EXISTS;

    private final java.util.Map<String, Constraint> fieldConstraints = new java.util.LinkedHashMap<>();

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

    public Existence getExistence() {
        return existence;
    }

    public void setExistence(Existence existence) {
        this.existence = (existence == null) ? Existence.EXISTS : existence;
    }

    public java.util.Map<String, Constraint> getFieldConstraints() {
        return fieldConstraints;
    }
}

