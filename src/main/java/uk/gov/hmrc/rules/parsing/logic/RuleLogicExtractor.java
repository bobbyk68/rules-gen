package uk.gov.hmrc.rules.parsing.logic;

public interface RuleLogicExtractor {

    /**
     * @param rawCol6 The raw Column 6 cell content (newlines preserved if possible).
     */
    LogicExtractionResult extract(String rawCol6);
}
