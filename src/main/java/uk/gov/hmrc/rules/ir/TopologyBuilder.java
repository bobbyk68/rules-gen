package uk.gov.hmrc.rules.ir;

import uk.gov.hmrc.rules.parsing.ParsedCondition;

import java.util.List;

public class TopologyBuilder {

    public record ParentGroup(String anchorKey, String parentEntityType, List<ParsedCondition> children) {

    }

    // ==========================================================
    // CHANGED FIELD: policy injected (single decision point)
    // ==========================================================
    private final IrPolicy policy;

    // ==========================================================
    // CHANGED: constructors (default to whitelist policy)
    // ==========================================================
    public TopologyBuilder() {
        this(new MergeWhitelistPolicy());
    }

    public TopologyBuilder(IrPolicy policy) {
        this.policy = policy;
    }

    // ==========================================================
    // CHANGED METHOD: build() now splits GI into 1 or 2 parents
    // depending on merge decision.
    //
    // Rules:
    // - If anchor != "GI": unchanged behaviour (group by anchor)
    // - If anchor == "GI" and exactly one PRIMARY + one SECONDARY:
    //     - MERGE     => one GI group with both children
    //     - NOT_MERGE => two GI groups: GI$P and GI$S
    //
    // NOTE: This is IR topology only. The actual join constraint
    // (GI$S != GI$P) comes later when generating DSLR.
    // ==========================================================
    public java.util.List<ParentGroup> build(java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition> parsedConditions) {
        java.util.Map<String, java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition>> byAnchor = new java.util.LinkedHashMap<>();

        for (uk.gov.hmrc.rules.parsing.ParsedCondition pc : parsedConditions) {
            byAnchor
                    .computeIfAbsent(pc.getParentAnchorKey(),
                            k -> new java.util.ArrayList<>())
                    .add(pc);
        }

        java.util.List<ParentGroup> result = new java.util.ArrayList<>();

        for (java.util.Map.Entry<String, java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition>> e : byAnchor.entrySet()) {
            String anchorKey = e.getKey();
            java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition> children = e.getValue();

            // Special handling for GI: decide MERGE vs NOT_MERGE
            if ("GI".equals(anchorKey)) {
                uk.gov.hmrc.rules.parsing.ParsedCondition primary = findByRole(children, uk.gov.hmrc.rules.parsing.ConditionRole.PRIMARY);
                uk.gov.hmrc.rules.parsing.ParsedCondition secondary = findByRole(children, uk.gov.hmrc.rules.parsing.ConditionRole.SECONDARY);

                boolean exactlyOneEach = primary != null && secondary != null && countRole(children, uk.gov.hmrc.rules.parsing.ConditionRole.PRIMARY) == 1
                        && countRole(children, uk.gov.hmrc.rules.parsing.ConditionRole.SECONDARY) == 1;

                if (exactlyOneEach) {
                    MergeDecision decision = policy.decideMerge(primary, secondary);

                    if (decision.isMerge()) {
                        String parentType = inferParentType(anchorKey);
                        result.add(new ParentGroup(anchorKey, parentType, children));
                        continue;
                    }

                    // NOT_MERGE => split into two parent scopes (synthetic anchors)
                    String parentType = inferParentType(anchorKey);

                    result.add(new ParentGroup("GI$P", parentType, java.util.List.of(primary)));
                    result.add(new ParentGroup("GI$S", parentType, java.util.List.of(secondary)));
                    continue;
                }
            }

            // Default behaviour (including GI when we don't have exactly 1+1 roles)
            String parentType = inferParentType(anchorKey);
            result.add(new ParentGroup(anchorKey, parentType, children));
        }

        return result;
    }

    // ==========================================================
    // CHANGED METHOD: inferParentType() based on anchor prefix
    // (supports GI$P / GI$S)
    // ==========================================================
    private String inferParentType(String anchorKey) {
        if (anchorKey == null) return "Unknown";

        if (anchorKey.startsWith("GI")) return "GoodsItem";
        if (anchorKey.startsWith("DECL")) return "Declaration";

        // fallback: keep your old behaviour
        return "GoodsItem";
    }

    // ==========================================================
    // NEW helper: find a condition by role (first match)
    // ==========================================================
    private uk.gov.hmrc.rules.parsing.ParsedCondition findByRole(
            java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition> children,
            uk.gov.hmrc.rules.parsing.ConditionRole role) {

        for (uk.gov.hmrc.rules.parsing.ParsedCondition pc : children) {
            if (pc.getRole() == role) return pc;
        }
        return null;
    }

    // ==========================================================
    // NEW helper: count role occurrences (to enforce 1 primary + 1 secondary)
    // ==========================================================
    private int countRole(java.util.List<uk.gov.hmrc.rules.parsing.ParsedCondition> children,
                          uk.gov.hmrc.rules.parsing.ConditionRole role) {
        int count = 0;
        for (uk.gov.hmrc.rules.parsing.ParsedCondition pc : children) {
            if (pc.getRole() == role) count++;
        }
        return count;
    }
}
