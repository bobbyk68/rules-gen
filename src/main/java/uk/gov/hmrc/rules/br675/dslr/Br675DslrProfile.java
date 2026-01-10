package uk.gov.hmrc.rules.br675.dslr;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.gov.hmrc.rules.dslr.DslrWhenBlock;
import uk.gov.hmrc.rules.parsing.ConditionRole;
import uk.gov.hmrc.rules.parsing.ParsedCondition;



public class Br675DslrProfile {

    public List<DslrWhenBlock> buildWhenBlocks(List<ParsedCondition> parsedConditions) {

        // In BR675 you typically end up with up to two “parent anchors”:
        // GI (GoodsItem) and DECL (Declaration). For now we output blocks
        // in the order they appear; grouping can come next step if needed.
        List<DslrWhenBlock> blocks = new ArrayList<>();

        for (ParsedCondition pc : parsedConditions) {

            boolean isIf = pc.getRole() == ConditionRole.PRIMARY;
            boolean isThen = pc.getRole() == ConditionRole.SECONDARY;

            // We only render blocks for PRIMARY and SECONDARY in BR675 emitter.
            if (!isIf && !isThen) {
                continue;
            }

            String label = pc.getFieldTypeLabel(); // e.g. "special procedure", "invoice amount"

            // Headline choice
            String headline;
            if (isIf) {
                headline = Br675DslrPhrasebook.headlineExists(label);
            } else {
                // THEN side
                ParsedCondition.Quantifier q = pc.getQuantifier();
                if (q == ParsedCondition.Quantifier.AT_LEAST_ONE) {
                    headline = Br675DslrPhrasebook.headlineNoMatchingExists(label);
                } else if (q == ParsedCondition.Quantifier.ALL) {
                    headline = Br675DslrPhrasebook.headlineExists(label);
                } else {
                    // default safe assumption for BR675 THEN:
                    headline = Br675DslrPhrasebook.headlineNoMatchingExists(label);
                }
            }

            DslrWhenBlock block = new DslrWhenBlock(headline);

            // Dash line choice
            String effectiveOperator = normalise(pc.getOperator());
            if (isThen && pc.getQuantifier() == ParsedCondition.Quantifier.ALL) {
                effectiveOperator = Br675DslrPhrasebook.invertOperator(effectiveOperator);
            }

            String fieldLabel = Br675DslrPhrasebook.dashFieldLabel(
                    pc.getFieldTypeLabel(),
                    pc.getFieldName()
            );

            // render dash lines from operator + values
            renderDashLines(block, effectiveOperator, fieldLabel, pc.getValues());

            blocks.add(block);
        }

        return blocks;
    }

    private void renderDashLines(DslrWhenBlock block,
                                 String op,
                                 String fieldLabel,
                                 List<String> values) {

        // Unary operators always produce a dash line, despite having no values.
        if ("IS_PROVIDED".equals(op)) {
            block.addLine(Br675DslrPhrasebook.dashIsProvided(fieldLabel));
            return;
        }
        if ("IS_NOT_PROVIDED".equals(op)) {
            block.addLine(Br675DslrPhrasebook.dashIsNotProvided(fieldLabel));
            return;
        }

        // Some patterns may be "EXISTS only" (no meaningful operator/values)
        // In BR675, we generally *want* at least one dash line. If there is no
        // operator/values, we emit no dash and rely on headline existence.
        if (values == null || values.isEmpty()) {
            return;
        }

        // Binary operators
        if ("IN".equals(op)) {
            block.addLine(Br675DslrPhrasebook.dashIn(fieldLabel, values));
            return;
        }
        if ("NOT_IN".equals(op)) {
            block.addLine(Br675DslrPhrasebook.dashNotIn(fieldLabel, values));
            return;
        }

        // single-value operators
        String v0 = values.get(0);

        if ("==".equals(op)) {
            block.addLine(Br675DslrPhrasebook.dashEquals(fieldLabel, v0));
            return;
        }
        if ("!=".equals(op)) {
            block.addLine(Br675DslrPhrasebook.dashNotEquals(fieldLabel, v0));
            return;
        }
        if ("<".equals(op)) {
            block.addLine(Br675DslrPhrasebook.dashLessThan(fieldLabel, v0));
            return;
        }

        // fallback: treat unknown as equals
        block.addLine(Br675DslrPhrasebook.dashEquals(fieldLabel, v0));
    }

    private String normalise(String op) {
        if (op == null) return "";
        return op.trim().toUpperCase(Locale.ROOT);
    }
}
