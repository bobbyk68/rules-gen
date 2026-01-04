package uk.gov.hmrc.rules.dsl;

public class DslRhsRenderer {

    public String rhs(String parentAlias, String entityType, String fieldName, String operator) {

        // Unary: invoiceAmount must be provided
        if ("IS_PROVIDED".equals(operator) || "IS_PRESENT".equals(operator)) {
            // In DRL this typically becomes: invoiceAmount != null
            return entityType + "( " + fieldName + " != null )";
        }

        if ("IN".equals(operator) || "NOT_IN".equals(operator)) {
            // {value} will be CSV in DSLR; expansion can split or use memberOf list, etc.
            // Keep simple now; you can refine later.
            return entityType + "( " + fieldName + " " + operatorToDrl(operator) + " ({value}) )";
        }

        return entityType + "( " + fieldName + " " + operatorToDrl(operator) + " \"{value}\" )";
    }

    private String operatorToDrl(String op) {
        return switch (op) {
            case "==" -> "==";
            case "!=" -> "!=";
            case "<"  -> "<";
            case ">"  -> ">";
            case "NOT_IN" -> "not in";
            case "IN" -> "in";
            default -> op;
        };
    }
}
