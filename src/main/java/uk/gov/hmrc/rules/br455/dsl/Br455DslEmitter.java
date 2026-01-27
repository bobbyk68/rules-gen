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
    public List<DslEntry> emitWhen(RuleModel model) {

        String ifCondition = model.parsed().ifText();
        Br455ListRule rule = parser.parse(ifCondition);

        uk.gov.hmrc.rules.br455.registry.Resolved r = registry.resolve(rule.fieldPath());
        String friendly = r.friendlyVar();

        DottedPathExpander expander = new DottedPathExpander();
        DottedPathExpander.ExpandedPath ep = expander.expand(r.propertyPath());

        // ------------------------------------------------------------
        // Build the FINAL leaf RHS exactly as you do today (unchanged)
        // ------------------------------------------------------------
        String guards = "";
        for (String g : ep.parentGuards()) {
            guards += (g + " != null,");
        }

        String bindTemplate = """
        %s : %s( %s %s : %s,
        %s != null && %s.trim().length() > 0 )
        $ref : RefDataSetFact( name == "{value}", $vals : values )
        $norm : String() from ( %s.trim() )
        """;

        String bind = String.format(
                bindTemplate,
                r.alias(),
                r.factClassSimpleName(),
                guards,
                friendly,
                r.propertyPath(),
                friendly,
                friendly,
                friendly
        );

        String listMembership = "String(this == $norm ) from $vals";

        String violation = (rule.mode() == Br455ListRule.Mode.MUST_EXIST_IN_LIST)
                ? "not (" + listMembership + ")"
                : listMembership;

        String leafRhs = bind + violation;

        // ------------------------------------------------------------
        // Emit BR675-style multi-line WHEN as multiple DSL entries
        // ------------------------------------------------------------
        List<DslEntry> out = new java.util.ArrayList<>();

        String root = rootKey(rule.fieldPath()); // e.g. ConsignmentShipment / Declaration / GoodsItem

        // A) Headline
        String headlineLhs = headlineForRoot(root);
        String headlineRhs = headlineRhsForRoot(root);

        DslKey headlineKey = new DslKey(
                "BR455",
                "condition",
                "SINGLE",
                root,
                root,
                stripRoot(rule.fieldPath()),
                "HEADLINE"
        );
        out.add(new DslEntry(headlineKey, "condition", headlineLhs, headlineRhs));

        // B) Parent "provided" lines
        for (String g : ep.parentGuards()) {

            String providedLhs = "- with " + humaniseForDslr(g) + " provided";
            String providedRhs = buildNullGuards(g);

            DslKey providedKey = new DslKey(
                    "BR455",
                    "condition",
                    "SINGLE",
                    root,
                    root,
                    g,
                    "PROVIDED"
            );

            out.add(new DslEntry(providedKey, "condition", providedLhs, providedRhs));
        }

        // C) Final leaf list line (uses your existing DRL)
        String leafWords =
                uk.gov.hmrc.rules.br455.format.Br455ThenMessageFormatter.friendlyPathNoDots(rule.fieldPath());

        String leafLhs = "- with " + leafWords + " " + (
                rule.mode() == Br455ListRule.Mode.MUST_EXIST_IN_LIST
                        ? "must exist in list {value}"
                        : "must not exist in list {value}"
        );

        DslKey leafKey = new DslKey(
                "BR455",
                "condition",
                "SINGLE",
                root,
                root,
                stripRoot(rule.fieldPath()),
                rule.mode().name()
        );

        out.add(new DslEntry(leafKey, "condition", leafLhs, leafRhs));

        return out;
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
