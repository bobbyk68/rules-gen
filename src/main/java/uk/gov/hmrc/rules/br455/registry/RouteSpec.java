package uk.gov.hmrc.rules.br455.registry;

public record RouteSpec(
        String factClassSimpleName,
        String factAlias,
        int startIndex // 1 = root-tail, 2 = segment1-tail
) { }
