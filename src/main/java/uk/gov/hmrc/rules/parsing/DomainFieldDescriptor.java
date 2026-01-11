package uk.gov.hmrc.rules.parsing;

public record DomainFieldDescriptor(String entityType, String parentAnchorKey, String fieldName,
                                    String fieldTypeLabel) {

}
