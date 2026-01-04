package uk.gov.hmrc.rules.dsl;

import uk.gov.hmrc.rules.emitter.Br675ThenRhsRenderer;
import uk.gov.hmrc.rules.emitter.Br675WhenRhsRenderer;
import uk.gov.hmrc.rules.ir.*;


import java.util.List;

public class Br675DslEmitter implements RuleSetDslEmitter {

    private final DslWording wording = new DslWording();
    private final Br675WhenRhsRenderer whenRhs = new Br675WhenRhsRenderer();
    private final Br675ThenRhsRenderer thenRhs = new Br675ThenRhsRenderer();

    @Override
    public boolean supports(String ruleSet) {
        return "BR675".equalsIgnoreCase(ruleSet);
    }

    @Override
    public DslEmission emit(RuleModel model) {

        java.util.List<DslEntry> when = new java.util.ArrayList<>();
        java.util.List<DslEntry> then = new java.util.ArrayList<>();

        // TEMP, just to prove the pipeline end-to-end:
        // 1) turn each FactConditionNode into a [when] DSL entry (very rough)
        // 1) turn each FactConditionNode into a [when] DSL entry (still rough)
        for (ConditionNode n : model.getConditions()) {
            if (n instanceof FactConditionNode fc) {

                String parentAnchorKey = inferAnchor(fc.getFactType()); // "GI" or "DECL"
                String quantifier = "AT_LEAST_ONE"; // TODO: carry real quantifier into IR node

                for (java.util.Map.Entry<String, Constraint> e : fc.getFieldConstraints().entrySet()) {
                    String field = e.getKey();
                    Constraint c = e.getValue();
                    String op = c.getOperator();

                    String lhs = "[when] " + fc.getFieldTypeLabel() + " - with " + field + " " + op + " {value}";
                    String rhs = field + " " + op + " {value}";

                    when.add(new DslEntry(
                            new DslKey(
                                    "BR675",
                                    "condition",
                                    quantifier,
                                    parentAnchorKey,
                                    fc.getFactType(),   // entityType
                                    field,
                                    op
                            ),
                            "when",
                            lhs,
                            rhs
                    ));
                }
            }
        }



        // 2) add one [then] DSL entry (also rough) from the action node
        for (ActionNode a : model.getActions()) {
            if (a instanceof EmitErrorActionNode emit) {

                String lhs = "[then] Emit " + emit.getBrCode() + " validation error";
                String rhs = "insert( emit(drools, " + emit.getBrCode() + ", \"" + emit.getDmsErrorCode() + "\" ) );";

                then.add(new DslEntry(
                        new DslKey(
                                "BR675",
                                "consequence",
                                "EMIT",
                                "DECL",
                                "ValidationResult",
                                "errorCode",
                                "=="
                        ),
                        "then",
                        lhs,
                        rhs
                ));
            }
        }


        return new DslEmission(when, then);
    }

    private static String inferAnchor(String factType) {
        if (factType == null) return "GI";
        return switch (factType) {
            case "Declaration", "DeclarationAmendment", "DeclarationCustomsOffice",
                 "DeclarationCurrency", "DeclarationAdditionalInformation" -> "DECL";
            default -> "GI";
        };
    }

    @Override
    public List<DslEntry> emitWhen(RuleModel model) {
        return List.of();
    }

    @Override
    public List<DslEntry> emitThen(RuleModel model) {
        return List.of();
    }
}
