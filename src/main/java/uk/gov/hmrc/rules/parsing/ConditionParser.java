package uk.gov.hmrc.rules.parsing;

public interface ConditionParser {

    ParsedCondition parseIf(String ifText);

    ParsedCondition parseThen(String thenText);
}
