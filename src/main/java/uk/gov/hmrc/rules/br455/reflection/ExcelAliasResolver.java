package uk.gov.hmrc.rules.br455.reflection;

public interface ExcelAliasResolver {

    // Version: 2026-01-23 v1.0.0

    /**
     * @return Java property name to use instead of excelToken, or null if no alias exists.
     */
    String translateToken(Class<?> currentType, String excelToken);
}
