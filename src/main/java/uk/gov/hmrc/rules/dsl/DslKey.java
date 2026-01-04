package uk.gov.hmrc.rules.dsl;

public record DslKey(
        String ruleSet,
        String section, // "condition" or "consequence"
        String quantifier, // "AT_LEAST_ONE" or "ALL"
        String parentAnchorKey, // "GI" or "DECL"
        String entityType,
        String fieldName,
        String operator
) {}

