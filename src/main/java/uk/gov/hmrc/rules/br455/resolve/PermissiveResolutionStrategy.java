package uk.gov.hmrc.rules.br455.resolve;

// Version: 2026-01-24
public final class PermissiveResolutionStrategy implements ResolutionStrategy {

    private final java.util.Map<String, String> lastResortRenames;

    public PermissiveResolutionStrategy(java.util.Map<String, String> lastResortRenames) {
        this.lastResortRenames = (lastResortRenames == null) ? java.util.Map.of() : lastResortRenames;
    }

    @Override
    public java.util.List<String> segmentCandidates(Class<?> currentType, String originalSegment) {

        java.util.ArrayList<String> out = new java.util.ArrayList<>();
        out.add(originalSegment);

        // Last-resort semantic renames (Excel name genuinely different)
        String mapped = lastResortRenames.get(originalSegment);
        if (mapped != null && !mapped.isBlank()) {
            out.add(mapped);
        }

        // De-dupe, preserve order
        java.util.LinkedHashSet<String> dedup = new java.util.LinkedHashSet<>(out);
        return new java.util.ArrayList<>(dedup);
    }

    @Override
    public java.util.Optional<String> tailCollapseCandidate(Class<?> currentType, String[] parts, int index) {

        // General tail collapse: join remaining segments into camelCase.
        // Example: ["type","code"] -> "typeCode"
        if (parts == null || index < 0 || index >= parts.length) {
            return java.util.Optional.empty();
        }
        if (index + 1 >= parts.length) {
            return java.util.Optional.empty();
        }

        String candidate = camelJoin(parts, index, parts.length);
        return java.util.Optional.of(candidate);
    }

    private String camelJoin(String[] parts, int fromInclusive, int toExclusive) {
        String first = (parts[fromInclusive] == null) ? "" : parts[fromInclusive];
        StringBuilder sb = new StringBuilder(first);

        for (int i = fromInclusive + 1; i < toExclusive; i++) {
            String p = parts[i];
            if (p == null || p.isBlank()) {
                continue;
            }
            sb.append(p.substring(0, 1).toUpperCase(java.util.Locale.ROOT));
            if (p.length() > 1) {
                sb.append(p.substring(1));
            }
        }
        return sb.toString();
    }
}
