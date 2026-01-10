package uk.gov.hmrc.rules.br455.dslr;

import uk.gov.hmrc.rules.br455.format.Br455ThenMessageFormatter;

public final class Br455DslrPhrasebook {

    private Br455DslrPhrasebook() {
    }

    public static String headlineFieldListCheck() {
        return "Field list check";
    }

    public static String dashMustExist(String fieldPath, String listName) {
        return "- " + Br455ThenMessageFormatter.friendlyPathNoDots(fieldPath) + " must exist in list " + listName;
    }

    public static String dashMustNotExist(String fieldPath, String listName) {
        return "- " + Br455ThenMessageFormatter.friendlyPathNoDots(fieldPath) + " must not exist in list " + listName;
    }
}
