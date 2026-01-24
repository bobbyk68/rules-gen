package uk.gov.hmrc.rules.br455.resolve;

// Version: 2026-01-24
public interface ResolutionStrategy {

    /**
     * Called only when DEFAULT reflection lookup fails for a segment.
     * Return candidate segment names to attempt (in order).
     */
    java.util.List<String> segmentCandidates(Class<?> currentType, String originalSegment);

    /**
     * Called only when DEFAULT lookup fails and segmentCandidates did not help.
     * Return a single candidate that collapses the remaining tail into one property name,
     * e.g. type.code -> typeCode.
     *
     * Return empty to disable tail collapse.
     */
    java.util.Optional<String> tailCollapseCandidate(Class<?> currentType, String[] parts, int index);
}
