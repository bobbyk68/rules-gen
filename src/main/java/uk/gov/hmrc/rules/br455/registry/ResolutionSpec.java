package uk.gov.hmrc.rules.br455.registry;

public record ResolutionSpec(
        String factClassSimpleName,
        String factAlias,
        int propertyStartIndex
) { }
