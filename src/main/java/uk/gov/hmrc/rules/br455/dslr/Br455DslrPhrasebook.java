package uk.gov.hmrc.rules.br455.dslr;

public final class Br455DslrPhrasebook {

    private Br455DslrPhrasebook() {
    }

    public static String headlineFieldListCheck() {
        return "Field list check";
    }

    public static String dashMustExist(String fieldPath, String listName) {
        return "- " + fieldPath + " must exist in list " + listName;
    }

    public static String dashMustNotExist(String fieldPath, String listName) {
        return "- " + fieldPath + " must not exist in list " + listName;
    }
}
