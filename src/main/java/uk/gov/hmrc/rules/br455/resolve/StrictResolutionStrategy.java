package uk.gov.hmrc.rules.br455.resolve;

// Version: 2026-01-24
public final class StrictResolutionStrategy implements ResolutionStrategy {

    @Override
    public java.util.List<String> segmentCandidates(Class<?> currentType, String originalSegment) {
        return java.util.List.of(originalSegment);
    }

    @Override
    public java.util.Optional<String> tailCollapseCandidate(Class<?> currentType, String[] parts, int index) {
        return java.util.Optional.empty();
    }
}
