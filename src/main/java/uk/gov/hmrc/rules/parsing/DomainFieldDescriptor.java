package uk.gov.hmrc.rules.parsing;

public class DomainFieldDescriptor {

    private final String entityType;
    private final String parentAnchorKey;
    private final String fieldName;
    private final String fieldTypeLabel;

    public DomainFieldDescriptor(String entityType,
                                 String parentAnchorKey,
                                 String fieldName,
                                 String fieldTypeLabel) {
        this.entityType = entityType;
        this.parentAnchorKey = parentAnchorKey;
        this.fieldName = fieldName;
        this.fieldTypeLabel = fieldTypeLabel;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getParentAnchorKey() {
        return parentAnchorKey;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldTypeLabel() {
        return fieldTypeLabel;
    }
}
