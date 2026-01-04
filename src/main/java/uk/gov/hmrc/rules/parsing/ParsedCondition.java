package uk.gov.hmrc.rules.parsing;

import java.util.List;
public class ParsedCondition {


    public enum Quantifier { AT_LEAST_ONE, ALL }
    Quantifier quantifier;     // <-- add this (or equivalent)
    private final String entityType;
    private final String parentAnchorKey;
    private final String fieldName;
    private final String operator;
    private final java.util.List<String> values;
    private final String fieldTypeLabel;
    private final ConditionRole role;

    public ParsedCondition(String entityType,
                           String parentAnchorKey,
                           String fieldName,
                           String operator,
                           java.util.List<String> values,
                           String fieldTypeLabel,
                           ConditionRole role) {
        this.entityType = entityType;
        this.parentAnchorKey = parentAnchorKey;
        this.fieldName = fieldName;
        this.operator = operator;
        this.values = java.util.List.copyOf(values);
        this.fieldTypeLabel = fieldTypeLabel;
        this.role = role;
    }

    public String getEntityType() {
        return entityType;
    }

    public Quantifier getQuantifier() {return quantifier;}

    public String getParentAnchorKey() {
        return parentAnchorKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOperator() {
        return operator;
    }

    public java.util.List<String> getValues() {
        return values;
    }

    public String getFieldTypeLabel() {
        return fieldTypeLabel;
    }

    public ConditionRole getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "ParsedCondition{" +
                "entityType='" + entityType + '\'' +
                ", parentAnchorKey='" + parentAnchorKey + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", operator='" + operator + '\'' +
                ", values=" + values +
                ", fieldTypeLabel='" + fieldTypeLabel + '\'' +
                ", role=" + role +
                '}';
    }

    // ==========================================================
    // CHANGED METHOD: stable canonical key for merge policy
    // Format: "<ANCHOR>|<ENTITY_TYPE>|<FIELD_NAME>"
    // Example: "GI|RequestedProcedure|code"
    // ==========================================================
    public String canonicalPathKey() {
        return norm(getParentAnchorKey()) + "|" + norm(getEntityType()) + "|" + norm(getFieldName());
    }

    private String norm(String s) {
        if (s == null) return "";
        return s.trim();
    }
}
