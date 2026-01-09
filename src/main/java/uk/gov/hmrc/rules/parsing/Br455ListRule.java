package uk.gov.hmrc.rules.parsing;

public record Br455ListRule(
        String fieldPath,   // ConsignmentShipment.BorderTransportMeans.nationality.code
        String listName,    // #ImportCountries
        Mode mode           // MUST_EXIST or MUST_NOT_EXIST
) {
    public enum Mode {
        MUST_EXIST_IN_LIST,
        MUST_NOT_EXIST_IN_LIST
    }

    public String fieldLeaf() {
        if (fieldPath == null || fieldPath.isBlank()) return "field";
        int i = fieldPath.lastIndexOf('.');
        return i >= 0 ? fieldPath.substring(i + 1) : fieldPath;
    }
}
