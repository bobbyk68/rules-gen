package uk.gov.hmrc.rules.br455.reflection;

public final class NoOpExcelAliasResolver implements ExcelAliasResolver {

    // Version: 2026-01-23 v1.0.0

    @Override
    public String translateToken(Class<?> currentType, String excelToken) {
        return null;
    }
}
