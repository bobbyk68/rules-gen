package uk.gov.hmrc.rules.br455.reflection;

// Version: 2026-01-24
public final class Br455ExcelAliasResolver implements ReflectionScalarPathResolver.ExcelAliasResolver {

    @Override
    public String toJavaName(Class<?> rootType,
                            String[] parts,
                            int startIndex,
                            int segmentIndex,
                            Class<?> currentType,
                            String spreadsheetName) {

        if (spreadsheetName == null || spreadsheetName.isBlank()) {
            return spreadsheetName;
        }

        // BR455 spreadsheet uses PascalCase for some object-ish segments:
        // "VatDeclaringParty" -> "vatDeclaringParty"
        // Keep camelCase segments as-is: "subRole", "code"
        if (Character.isUpperCase(spreadsheetName.charAt(0))) {
            return decapitalize(spreadsheetName);
        }

        return spreadsheetName;
    }

    private String decapitalize(String s) {
        if (s.length() == 1) {
            return s.toLowerCase(java.util.Locale.ROOT);
        }
        return s.substring(0, 1).toLowerCase(java.util.Locale.ROOT) + s.substring(1);
    }
}
