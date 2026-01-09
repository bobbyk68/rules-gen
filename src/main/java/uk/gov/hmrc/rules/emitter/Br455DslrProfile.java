package uk.gov.hmrc.rules.emitter;

import java.util.List;
import uk.gov.hmrc.rules.br455.Br455ListRule;

public final class Br455DslrProfile {

    public DslrWhenBlock buildWhenBlock(Br455ListRule rule) {

        DslrWhenBlock block = new DslrWhenBlock(Br455DslrPhrasebook.headlineFieldListCheck());

        String dash = switch (rule.mode()) {
            case MUST_EXIST_IN_LIST -> Br455DslrPhrasebook.dashMustExist(rule.fieldPath(), rule.listName());
            case MUST_NOT_EXIST_IN_LIST -> Br455DslrPhrasebook.dashMustNotExist(rule.fieldPath(), rule.listName());
        };

        // IMPORTANT: addLine expects String (your earlier error)
        block.addLine(dash);

        return block;
    }

    public List<DslrWhenBlock> buildWhenBlocks(Br455ListRule rule) {
        return List.of(buildWhenBlock(rule));
    }
}
