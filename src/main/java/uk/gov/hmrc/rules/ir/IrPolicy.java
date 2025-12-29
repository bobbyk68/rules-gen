package uk.gov.hmrc.rules.ir;

public interface IrPolicy {
    MergeDecision decideMerge(uk.gov.hmrc.rules.parsing.ParsedCondition ifCondition,
                              uk.gov.hmrc.rules.parsing.ParsedCondition thenCondition);
}
