package uk.gov.hmrc.rules.ir;

public enum MergeDecision {
    MERGE,
    NOT_MERGE;

    public boolean isMerge() {
        return this == MERGE;
    }
}
