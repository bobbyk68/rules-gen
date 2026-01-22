package uk.gov.hmrc.rules.br455.registry;

public record Resolved(
        String factClassSimpleName,
        String factAlias,
        String bindPath,
        String dslFieldName,
        String fieldVar
) { }
