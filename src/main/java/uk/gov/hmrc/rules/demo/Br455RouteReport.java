package uk.gov.hmrc.rules.demo;

import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public final class Br455RouteReport {

    private static final Pattern FIELD_PATH_PATTERN =
            Pattern.compile("^(Declaration|ConsignmentShipment|GoodsItem)\\..+");

    private static final int SAMPLE_LIMIT = 6;

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: Br455RouteReport <path-to-merged.csv> [--emit-route-skeleton]");
            System.exit(2);
        }

        Path csv = Path.of(args[0]);
        boolean emitSkeleton = args.length >= 2 && "--emit-route-skeleton".equalsIgnoreCase(args[1]);

        Map<RouteKey, Stats> statsByKey = new HashMap<>();

        long lines = 0;
        long matched = 0;
        long skipped = 0;

        try (BufferedReader br = Files.newBufferedReader(csv, StandardCharsets.UTF_8)) {
            String line;
            while ((line = br.readLine()) != null) {
                lines++;
                String trimmed = line.trim();
                if (trimmed.isEmpty()) continue;

                List<String> cols = parseCsvLine(trimmed);

                String fieldPath = findFieldPath(cols);
                if (fieldPath == null) {
                    skipped++;
                    continue;
                }
                matched++;

                String[] parts = fieldPath.split("\\.");
                if (parts.length < 2) {
                    skipped++;
                    continue;
                }

                String root = parts[0].trim();
                String seg1 = parts[1].trim();

                RouteKey key = new RouteKey(root, seg1);
                Stats st = statsByKey.computeIfAbsent(key, k -> new Stats());
                st.count++;
                st.samples.add(fieldPath);
                if (st.samples.size() > SAMPLE_LIMIT) {
                    // keep deterministic set size; remove "oldest" by rebuilding as insertion order set
                    st.samples = shrinkSet(st.samples, SAMPLE_LIMIT);
                }

                // capture rule ids if they exist (optional, but handy)
                List<String> ruleIds = extractRuleIds(cols);
                for (String rid : ruleIds) {
                    st.ruleIds.add(rid);
                    if (st.ruleIds.size() > 20) {
                        st.ruleIds = shrinkSet(st.ruleIds, 20);
                    }
                }
            }
        }

        System.out.println("BR455 Route Report");
        System.out.println("File: " + csv.toAbsolutePath());
        System.out.println("Lines: " + lines + " | Matched fieldPaths: " + matched + " | Skipped: " + skipped);
        System.out.println();

        List<Map.Entry<RouteKey, Stats>> rows = new ArrayList<>(statsByKey.entrySet());
        rows.sort((a, b) -> {
            int rootCmp = a.getKey().root.compareTo(b.getKey().root);
            if (rootCmp != 0) return rootCmp;
            int countCmp = Long.compare(b.getValue().count, a.getValue().count);
            if (countCmp != 0) return countCmp;
            return a.getKey().segment1.compareTo(b.getKey().segment1);
        });

        printTable(rows);

        if (emitSkeleton) {
            System.out.println();
            System.out.println("Route table skeleton (paste into RouteTables.routes())");
            System.out.println("------------------------------------------------------");
            emitRouteSkeleton(rows);
        }
    }

    private static void printTable(List<Map.Entry<RouteKey, Stats>> rows) {
        String fmtHeader = "%-20s %-30s %8s  %s%n";
        String fmtRow    = "%-20s %-30s %8d  %s%n";

        System.out.printf(fmtHeader, "Root", "Segment1", "Count", "Samples");
        System.out.printf(fmtHeader, "--------------------", "------------------------------", "--------", "------");

        for (Map.Entry<RouteKey, Stats> e : rows) {
            RouteKey k = e.getKey();
            Stats st = e.getValue();

            String sample = st.samples.stream().findFirst().orElse("");
            System.out.printf(fmtRow, k.root, k.segment1, st.count, sample);

            // print remaining samples indented
            boolean first = true;
            for (String s : st.samples) {
                if (first) { first = false; continue; }
                System.out.printf("%-20s %-30s %8s  %s%n", "", "", "", s);
            }

            // show a few rule ids as hint (optional)
            if (!st.ruleIds.isEmpty()) {
                System.out.printf("%-20s %-30s %8s  ruleIds: %s%n", "", "", "",
                        String.join(", ", st.ruleIds));
            }

            System.out.println();
        }
    }

    private static void emitRouteSkeleton(List<Map.Entry<RouteKey, Stats>> rows) {
        // only emits entries for segment1 under a root; you still decide which are child facts vs root fact.
        // This is a starter list: you’ll replace Fact/alias with correct ones for the child facts.
        for (Map.Entry<RouteKey, Stats> e : rows) {
            RouteKey k = e.getKey();
            System.out.println("// " + k.root + "." + k.segment1 + "  (count=" + e.getValue().count + ")");
            System.out.println("m.put(new RouteKey(\"" + k.root + "\", \"" + k.segment1 + "\"),");
            System.out.println("        new RouteSpec(\"<REPLACE_WITH_FACT>\", \"$<alias>\", 2));");
            System.out.println();
        }
    }

    private static String findFieldPath(List<String> cols) {
        for (String c : cols) {
            if (c == null) continue;
            String s = c.trim();
            if (s.isEmpty()) continue;
            if (FIELD_PATH_PATTERN.matcher(s).matches()) {
                return s;
            }
        }
        return null;
    }

    private static List<String> extractRuleIds(List<String> cols) {
        // Rule ids appear before the fieldPath column.
        // We capture BR455_XXX tokens we see.
        List<String> out = new ArrayList<>();
        for (String c : cols) {
            if (c == null) continue;
            String s = c.trim();
            if (s.startsWith("BR455_")) {
                // strip anything accidental
                int end = s.indexOf(' ');
                if (end > 0) s = s.substring(0, end);
                out.add(s);
            }
            // stop early if we hit fieldPath
            if (FIELD_PATH_PATTERN.matcher(s).matches()) break;
        }
        // de-dupe while preserving order
        LinkedHashSet<String> dedup = new LinkedHashSet<>(out);
        return new ArrayList<>(dedup);
    }

    private static LinkedHashSet<String> shrinkSet(LinkedHashSet<String> in, int limit) {
        LinkedHashSet<String> out = new LinkedHashSet<>();
        int i = 0;
        for (String s : in) {
            out.add(s);
            i++;
            if (i >= limit) break;
        }
        return out;
    }

    // Minimal CSV parser (handles commas inside quotes)
    private static List<String> parseCsvLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                // if double quote inside quoted string -> escaped quote
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
                continue;
            }

            if (ch == ',' && !inQuotes) {
                out.add(cur.toString().trim());
                cur.setLength(0);
                continue;
            }

            cur.append(ch);
        }

        out.add(cur.toString().trim());
        return out;
    }

    private record RouteKey(String root, String segment1) { }

    private static final class Stats {
        long count = 0;
        LinkedHashSet<String> samples = new LinkedHashSet<>();
        LinkedHashSet<String> ruleIds = new LinkedHashSet<>();
    }

    // Keep whatever you already have; this is just here so compilation is standalone if you copy/paste.
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    // Not used in the report; your real resolver already has this.
    @SuppressWarnings("unused")
    private static String toDroolsPropertyPath(String[] parts, int startIndex) {
        StringBuilder sb = new StringBuilder();
        for (int i = startIndex; i < parts.length; i++) {
            if (i > startIndex) sb.append('.');
            sb.append(parts[i].trim());
        }
        return sb.toString();
    }
}
