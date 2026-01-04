package uk.gov.hmrc.rules.dsl;

public class DslWording {

    public String lhsText(String quantifier, String parentFact, String label, String fieldName, String operator) {
        String q = "ALL".equals(quantifier) ? "All" : "There is at least one";

        // unary operators:
        if ("IS_PROVIDED".equals(operator) || "IS_PRESENT".equals(operator)) {
            return q + " " + parentFact + " " + label + " must be provided";
        }

        // IN / NOT_IN
        if ("IN".equals(operator)) {
            return q + " " + parentFact + " " + label + " - with " + fieldName + " must be one of {value}";
        }
        if ("NOT_IN".equals(operator)) {
            return q + " " + parentFact + " " + label + " - with " + fieldName + " must not be one of {value}";
        }

        // standard binary
        String opWord = switch (operator) {
            case "==" -> "equals";
            case "!=" -> "must not equal";
            case "<"  -> "must be less than";
            case ">"  -> "must be greater than";
            default   -> operator;
        };

        return q + " " + parentFact + " " + label + " - with " + fieldName + " " + opWord + " {value}";
    }
}
