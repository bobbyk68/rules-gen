package uk.gov.hmrc.rules.ir;

import uk.gov.hmrc.rules.parsing.ConditionRole;

public abstract class ConditionNode {

    private ConditionRole role = ConditionRole.OTHER;
    private String fieldTypeLabel;

    public ConditionRole getRole() {
        return role;
    }

    public void setRole(ConditionRole role) {
        this.role = role;
    }

    public String getFieldTypeLabel() {
        return fieldTypeLabel;
    }

    public void setFieldTypeLabel(String fieldTypeLabel) {
        this.fieldTypeLabel = fieldTypeLabel;
    }
}
