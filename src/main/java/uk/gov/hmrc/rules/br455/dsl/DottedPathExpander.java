package uk.gov.hmrc.rules.br455.dsl;

public final class DottedPathExpander {
    // Version: 2026-01-25 v2.0.0

    public record ExpandedPath(
            java.util.List<String> parentGuards,
            String fullPath
    ) {}

    public ExpandedPath expand(String dottedPath) {
        String path = safeTrim(dottedPath);

        if (path.isEmpty()) {
            return new ExpandedPath(java.util.List.of(), "");
        }

        String[] parts = path.split("\\.");
        if (parts.length == 1) {
            // Single segment: no parents to guard, fullPath is itself
            return new ExpandedPath(java.util.List.of(), parts[0]);
        }

        java.util.List<String> guards = new java.util.ArrayList<>();

        // Build prefixes up to (length-2) inclusive
        // For xx.yy.zz (len=3): i=0 -> xx, i=1 -> xx.yy
        for (int i = 0; i <= parts.length - 2; i++) {
            guards.add(joinPrefix(parts, i));
        }

        return new ExpandedPath(java.util.List.copyOf(guards), path);
    }

    private String joinPrefix(String[] parts, int endInclusive) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= endInclusive; i++) {
            if (i > 0) sb.append('.');
            sb.append(parts[i].trim());
        }
        return sb.toString();
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
