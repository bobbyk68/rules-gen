package uk.gov.hmrc.rules.br455;

public final class Br455ThenMessage {

    private Br455ThenMessage() {
    }

    public static String build(Br455ListRule rule) {
        String field = rule.fieldLeaf();
        String list = rule.listName();

        return switch (rule.mode()) {
            case MUST_EXIST_IN_LIST ->
                    field + " does not exist in list " + list;
            case MUST_NOT_EXIST_IN_LIST ->
                    field + " exists in list " + list;
        };
    }
}
