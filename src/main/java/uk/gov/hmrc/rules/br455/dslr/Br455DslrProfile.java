package uk.gov.hmrc.rules.br455.dslr;

import java.util.List;
import uk.gov.hmrc.rules.br455.Br455ListRule;
import uk.gov.hmrc.rules.dslr.DslrLine;
import uk.gov.hmrc.rules.dslr.DslrWhenBlock;

import static uk.gov.hmrc.rules.br455.Br455ListRule.Mode.MUST_EXIST_IN_LIST;

public final class Br455DslrProfile {

    public DslrWhenBlock buildWhenBlock(Br455ListRule rule) {

        DslrWhenBlock block = new DslrWhenBlock(Br455DslrPhrasebook.headlineFieldListCheck());

        String dash = switch (rule.mode()) {
            case MUST_EXIST_IN_LIST -> Br455DslrPhrasebook.dashMustExist(rule.fieldPath(), rule.listName());
            case MUST_NOT_EXIST_IN_LIST -> Br455DslrPhrasebook.dashMustNotExist(rule.fieldPath(), rule.listName());
        };

        // IMPORTANT: addLine expects String (matches your comment)
        block.addLine(dash);

        return block;
    }

    public java.util.List<DslrWhenBlock> buildWhenBlocks(Br455ListRule rule) {
        return java.util.List.of(buildWhenBlock(rule));
    }
}
