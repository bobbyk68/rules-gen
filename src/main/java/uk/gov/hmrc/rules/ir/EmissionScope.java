package uk.gov.hmrc.rules.ir;

public final class EmissionScope {
    private final java.util.Map<String, String> seqVarByAnchorKey = new java.util.HashMap<>();

    private final java.util.Set<String> emittedAnchorRhs = new java.util.HashSet<>();

    public boolean markAnchorEmitted(String rhs) {
        if (rhs == null || rhs.isBlank()) return false;
        return emittedAnchorRhs.add(rhs);  // true first time, false if already seen
    }

    public java.util.Set<String> emittedAnchorRhs() {
        return java.util.Collections.unmodifiableSet(emittedAnchorRhs);
    }
    public boolean hasEmittedAnchor(String anchorRhs) {
        return emittedAnchorRhs.contains(anchorRhs);
    }
}
