package uk.gov.hmrc.rules.br455.dsl;

import uk.gov.hmrc.rules.br455.Br455ListRule;
import uk.gov.hmrc.rules.br455.format.Br455ThenMessageFormatter;
import uk.gov.hmrc.rules.br455.lookup.Br455LeafResolver;
import uk.gov.hmrc.rules.br455.parsing.Br455IfParser;
import uk.gov.hmrc.rules.br455.registry.Br455RootFactRegistry;
import uk.gov.hmrc.rules.br455.registry.Resolved;
import uk.gov.hmrc.rules.br455.then.Br455ThenRhsBuilder;
import uk.gov.hmrc.rules.dsl.DslEmission;
import uk.gov.hmrc.rules.dsl.DslEntry;
import uk.gov.hmrc.rules.dsl.DslKey;
import uk.gov.hmrc.rules.dsl.RuleSetDslEmitter;
import uk.gov.hmrc.rules.ir.RuleModel;

import java.util.List;

public final class Br455DslEmitter implements RuleSetDslEmitter {

    private final Br455IfParser parser = new Br455IfParser();
    private final Br455RootFactRegistry registry = new Br455RootFactRegistry();

    @Override
    public boolean supports(String ruleSet) {
        if (ruleSet == null) return false;
        return ruleSet.trim().equalsIgnoreCase("BR455");
    }

    private static String fieldVar(String propertyPath) {
        String leaf = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
        return "$" + leaf;
    }

    @Override
    public java.util.List<DslEntry> emitWhen(RuleModel model) {

        String ifCondition = model.parsed().ifText();
        Br455ListRule rule = parser.parse(ifCondition);

        // Resolver still used for alias + fact type
        uk.gov.hmrc.rules.br455.registry.Resolved r = registry.resolve(rule.fieldPath());

        // Canonical dotted path for *both* DSL + DSLR shape/text.
        // This is the critical change.
        String canonicalPath = stripRoot(rule.fieldPath()); // e.g. "borderTransportMeans.nationality.code"

        // Expand the canonical path to generate 1 DSL line per guard (BR675 style)
        DottedPathExpander expander = new DottedPathExpander();
        DottedPathExpander.ExpandedPath ep = expander.expand(canonicalPath);

        java.util.List<DslEntry> out = new java.util.ArrayList<>();

        // ---------------------------
        // 1) Headline: "<Root> exists"
        // ---------------------------
        String rootKey = rootKey(rule.fieldPath());     // e.g. "ConsignmentShipment"
        String rootVar = lowerCamel(rootKey);           // e.g. "consignmentShipment"

        String headlineLhs = headlineExistsLhs(rootKey);
        String headlineRhs = r.alias() + " : " + r.factClassSimpleName() + "( " + rootVar + " != null )";

        DslKey headlineKey = new DslKey(
                "BR455",
                "condition",
                "SINGLE",
                rootKey,
                rootKey,
                canonicalPath,
                "EXISTS"
        );

        out.add(new DslEntry(headlineKey, "condition", headlineLhs, headlineRhs));

        // -----------------------------------------
        // 2) One "- with ... provided" per parent
        //    (this is where your missing DSL line was)
        // -----------------------------------------
        for (String g : ep.parentGuards()) {
            String dashLhs = dashProvidedLhs(friendlyWordsFromDottedPath(g));
            String dashRhs = g + " != null";

            DslKey dashKey = new DslKey(
                    "BR455",
                    "condition",
                    "SINGLE",
                    rootKey,
                    rootKey,
                    g,
                    "PROVIDED"
            );

            out.add(new DslEntry(dashKey, "condition", dashLhs, dashRhs));
        }

        // -------------------------------------------------------
        // 3) Final dash: "... code must exist in list ImportCountries"
        //    LHS uses canonicalPath so "code" appears when present.
        // -------------------------------------------------------
        String finalDashLhs = dashListMembershipLhs(
                friendlyWordsFromDottedPath(canonicalPath),
                rule.mode(),
                rule.listName()
        );

        String isValid = "isValidCode(" + stripTrailingDotCode(canonicalPath) + ", CodeListType.{value}, asOf, codeListLookup)";
        String finalDashRhs = (rule.mode() == Br455ListRule.Mode.MUST_NOT_EXIST_IN_LIST)
                ? "!" + isValid
                : isValid;

        DslKey finalKey = new DslKey(
                "BR455",
                "condition",
                "SINGLE",
                rootKey,
                rootKey,
                canonicalPath,
                (rule.mode() == Br455ListRule.Mode.MUST_NOT_EXIST_IN_LIST) ? "NOT_IN_LIST" : "IN_LIST"
        );

        out.add(new DslEntry(finalKey, "condition", finalDashLhs, finalDashRhs));

        return out;
    }

    private static String stripTrailingDotCode(String s) {
        if (s == null) return "";
        String t = s.trim();
        return t.endsWith(".code") ? t.substring(0, t.length() - ".code".length()) : t;
    }


    private static String lowerCamel(String s) {
        if (s == null || s.isBlank()) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Produces "Consignment shipment exists" (headline).
     * RootKey comes in as PascalCase ("ConsignmentShipment").
     */
    private static String headlineExistsLhs(String rootKey) {
        return splitPascalWords(rootKey).toLowerCase() + " exists";
    }

    /**
     * Produces "- with exportation country provided"
     */
    private static String dashProvidedLhs(String words) {
        return "- with " + words + " provided";
    }

    /**
     * Produces "- with exportation country country code must exist in list ImportCountries"
     * or "... must not exist ..."
     */
    private static String dashListMembershipLhs(String words, Br455ListRule.Mode mode, String listName) {
        String verb = (mode == Br455ListRule.Mode.MUST_NOT_EXIST_IN_LIST) ? "must not exist" : "must exist";
        return "- with " + words + " " + verb + " in list " + listName;
    }

    /**
     * Turns "exportationCountry.country.code" into "exportation country country code"
     * (yes: duplicates can happen; BR675 has them too).
     */
    private static String friendlyWordsFromDottedPath(String dotted) {
        if (dotted == null || dotted.isBlank()) return "";
        String[] parts = dotted.split("\\.");
        java.util.List<String> words = new java.util.ArrayList<>();
        for (String p : parts) {
            if (p == null || p.isBlank()) continue;
            words.add(splitCamelWords(p).toLowerCase());
        }
        return String.join(" ", words);
    }

    private static String splitCamelWords(String s) {
        // "exportationCountry" -> "exportation Country"
        return s.replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    private static String splitPascalWords(String s) {
        // "ConsignmentShipment" -> "Consignment Shipment"
        return s.replaceAll("([a-z])([A-Z])", "$1 $2");
    }

    // Version: 2026-01-27
    private static String headlineForRoot(String rootKey) {
        if (rootKey == null || rootKey.isBlank()) return "Object exists";

        String spaced = rootKey
                .replaceAll("([a-z0-9])([A-Z])", "$1 $2")
                .trim()
                .toLowerCase(java.util.Locale.ROOT);

        String pretty = Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
        return pretty + " exists";
    }

    // Version: 2026-01-27
    private static String humaniseForDslr(String dottedPath) {
        if (dottedPath == null || dottedPath.isBlank()) return "";
        return dottedPath
                .replace(".", " ")
                .replaceAll("([a-z0-9])([A-Z])", "$1 $2")
                .trim()
                .toLowerCase(java.util.Locale.ROOT);
    }

    // Version: 2026-01-27
    private static String buildNullGuards(String dottedPath) {
        if (dottedPath == null || dottedPath.isBlank()) return "";
        String[] parts = dottedPath.split("\\.");
        StringBuilder sb = new StringBuilder();
        String current = "";
        for (int i = 0; i < parts.length; i++) {
            current = (i == 0) ? parts[i] : current + "." + parts[i];
            if (sb.length() > 0) sb.append(", ");
            sb.append(current).append(" != null");
        }
        return sb.toString();
    }

    // Version: 2026-01-27
    private static String headlineRhsForRoot(String rootKey) {
        // KEEP THIS ALIGNED WITH BR675'S ROOT BINDINGS.
        // These are safe placeholders until you swap them to your real fact patterns.
        if ("ConsignmentShipment".equals(rootKey)) {
            return "$cons : ConsignmentShipmentFact( consignmentShipment != null )";
        }
        if ("Declaration".equals(rootKey)) {
            return "$dec : DeclarationFact( declaration != null )";
        }
        if ("GoodsItem".equals(rootKey)) {
            return "$gi : GoodsItemFact( goodsItem != null )";
        }
        return "true";
    }



    // =========================
// CHANGED METHOD: emitThen()
// =========================
    @Override
    public List<DslEntry> emitThen(RuleModel model) {

        String ifCondition = model.ruleRow().ifCondition();
        Br455ListRule rule = parser.parse(ifCondition);

        // DSL THEN LHS (human-readable key)
        String lhs = Br455ThenMessageFormatter.buildThenDslLhs(
                "BR455",
                rule.fieldPath(),
                rule.mode(),
                rule.listName()
        );

        // Get the correct root alias ($d / $cs / $gi)
        Resolved r = registry.resolve(rule.fieldPath());
        String rootAlias = r.alias();

        // Build the *typed* RHS: insert(emitter.emit("BR455", $gi, Br455Leaf.VM_TYPE));
        Br455ThenRhsBuilder rhsBuilder = new Br455ThenRhsBuilder(
                new Br455LeafResolver(Br455LeafResolver.Mode.DEMO)
        );

        String rhs = rhsBuilder.buildDslRhs("BR455", rootAlias, rule.fieldPath(),rule.listName());

        DslKey key = new DslKey(
                "BR455",
                "then",
                "SINGLE",
                rootKey(rule.fieldPath()),
                rootKey(rule.fieldPath()),
                stripRoot(rule.fieldPath()),
                "EMIT_TYPED"
        );

        return List.of(new DslEntry(key, "then", lhs, rhs));
    }

    @Override
    public DslEmission emit(RuleModel model) {
        return new DslEmission(emitWhen(model), emitThen(model));
    }

    private static String stripRoot(String fieldPath) {
        if (fieldPath == null) return "";
        String s = fieldPath.trim();
        if (s.startsWith("Declaration.")) return s.substring("Declaration.".length());
        if (s.startsWith("ConsignmentShipment.")) return s.substring("ConsignmentShipment.".length());
        if (s.startsWith("GoodsItem.")) return s.substring("GoodsItem.".length());
        return s;
    }

    private static String rootKey(String fieldPath) {
        if (fieldPath == null) return "UNKNOWN";
        String s = fieldPath.trim();
        int idx = s.indexOf('.');
        return idx > 0 ? s.substring(0, idx) : s;
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
