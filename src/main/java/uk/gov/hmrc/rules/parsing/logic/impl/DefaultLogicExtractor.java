package uk.gov.hmrc.rules.parsing.logic.impl;

import uk.gov.hmrc.rules.parsing.logic.LogicExtractionResult;
import uk.gov.hmrc.rules.parsing.logic.RuleLogicExtractor;

public final class DefaultLogicExtractor implements RuleLogicExtractor {

    @Override
    public LogicExtractionResult extract(String rawCol6) {
        // If we don't understand the ruleset, treat everything as "ifText" and leave then/message empty.
        String raw = rawCol6 == null ? "" : rawCol6.trim();
        return LogicExtractionResult.of(raw, "", "");
    }
}
