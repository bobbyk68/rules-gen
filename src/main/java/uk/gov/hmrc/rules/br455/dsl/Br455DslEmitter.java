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
    // Version: 2026-01-27
    @Override
    public List<DslEntry> emitWhen(RuleModel model) {

        String ifCondition = model.parsed().ifText();
        Br455ListRule rule = parser.parse(ifCondition);

        uk.gov.hmrc.rules.br455.registry.Resolved r = registry.resolve(rule.fieldPath());
        String friendly = r.friendlyVar();

        DottedPathExpander expander = new DottedPathExpander();
        DottedPathExpander.ExpandedPath ep = expander.expand(r.propertyPath());

        List<DslEntry> out = new java.util.ArrayList<>();

        // ------------------------------------------------------------
        // 1) HEADLINE (no dash in DSLR)
        // LHS must match the DSLR headline exactly (constructor string).
        // ------------------------------------------------------------
        {
            String lhs = "Consignment shipment exists";

            // Keep RHS simple and consistent with your DSL style:
            // a single guard that makes the root exist.
            // NOTE: adjust this if your actual bound variable / fact differs.
            // If you already bind $cons elsewhere, use that pattern instead.
            String rhs = "$cons : ConsignmentShipmentFact( consignmentShipment != null )";

            DslKey key = new DslKey(
                    "BR455",
                    "condition",
                    "SINGLE",
                    rootKey(rule.fieldPath()),
                    rootKey(rule.fieldPath()),
                    "ConsignmentShipment", // fieldName-ish for uniqueness
                    "HEADLINE"
            );

            out.add(new DslEntry(key, "condition", lhs, rhs));
        }

        // ------------------------------------------------------------
        // 2) PROVIDED lines (dashed in DSLR): one per parent guard
        // ep.parentGuards() are things like:
        //   departureTransportMeans
        //   departureTransportMeans.mode
        // etc
        // ------------------------------------------------------------
        for (String g : ep.parentGuards()) {

            // DSLR line (must exactly match what Br455DslrProfile emits)
            String lhs = "- with " + humaniseForDslr(g) + " provided";

            // RHS guards for this parent
            // Example:
            //   departureTransportMeans != null
            // or for a deep guard, we can include all segments up to it.
            String rhs = buildNullGuards(g);

            DslKey key = new DslKey(
                    "BR455",
                    "condition",
                    "SINGLE",
                    rootKey(rule.fieldPath()),
                    rootKey(rule.fieldPath()),
                    g,                 // fieldName: parent guard path
                    "PROVIDED"
            );

            out.add(new DslEntry(key, "condition", lhs, rhs));
        }

        // ------------------------------------------------------------
        // 3) FINAL LIST predicate (dashed in DSLR)
        // Keep your existing bind+membership+violation approach.
        // LHS must match DSLR final line text and use {list} placeholder.
        // ------------------------------------------------------------
        {
            // Reuse your existing bind / violation logic as-is
            String guards = "";
            for (String g : ep.parentGuards()) {
                guards += (g + " != null,");
            }

            String bindTemplate = """
            %s : %s( %s %s : %s,
            %s != null && %s.trim().length() > 0 )
            $ref : RefDataSetFact( name == "{list}", $vals : values )
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
                    friendly,
                    friendly
            );

            String listMembership = "String(this == $norm ) from $vals";

            String violation = (rule.mode() == Br455ListRule.Mode.MUST_EXIST_IN_LIST)
                    ? "not (" + listMembership + ")"
                    : listMembership;

            String rhs = bind + violation;

            // DSLR final line: "- with <leaf words> must exist in list {list}"
            // IMPORTANT: keep "{list}" placeholder in the sentence.
            String leafWords = uk.gov.hmrc.rules.br455.format.Br455ThenMessageFormatter
                    .friendlyPathNoDots(rule.fieldPath());

            String lhs = "- with " + leafWords + " " + (
                    rule.mode() == Br455ListRule.Mode.MUST_EXIST_IN_LIST
                            ? "must exist in list {list}"
                            : "must not exist in list {list}"
            );

            DslKey key = new DslKey(
                    "BR455",
                    "condition",
                    "SINGLE",
                    rootKey(rule.fieldPath()),
                    rootKey(rule.fieldPath()),
                    stripRoot(rule.fieldPath()),
                    rule.mode().name()
            );

            out.add(new DslEntry(key, "condition", lhs, rhs));
        }

        return out;
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
    private static String humaniseForDslr(String dottedPath) {
        if (dottedPath == null || dottedPath.isBlank()) return "";
        // "departureTransportMeans.mode" -> "departure transport means mode"
        String spaced = dottedPath
                .replace(".", " ")
                .replaceAll("([a-z0-9])([A-Z])", "$1 $2")
                .trim();
        return spaced.toLowerCase(java.util.Locale.ROOT);
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
