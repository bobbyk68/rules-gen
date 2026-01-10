package uk.gov.hmrc.rules.br455.dsl;

import uk.gov.hmrc.rules.br455.parsing.Br455IfParser;
import uk.gov.hmrc.rules.br455.Br455ListRule;
import uk.gov.hmrc.rules.br455.registry.Br455RootFactRegistry;
import uk.gov.hmrc.rules.br455.format.Br455ThenMessageFormatter;
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

    @Override
    public List<DslEntry> emitWhen(RuleModel model) {

        String ifCondition = model.ruleRow().ifCondition();
        // ---------------------------------------------------------

        Br455ListRule rule = parser.parse(ifCondition);
        Br455RootFactRegistry.Resolved r = registry.resolve(rule.fieldPath());

        // Bind the domain value (NO eval)
        // e.g. $d : DeclarationFact( $v : invoiceAmount.unitType.code )
        String bind = r.alias() + " : " + r.factClassSimpleName()
                + "( $v : " + r.propertyPath() + " )";

        // Membership violation pattern
        // MUST_EXIST -> violation is "not RefDataEntry(listName == X, value == $v)"
        // MUST_NOT   -> violation is "RefDataEntry(listName == X, value == $v)"
        String listName = rule.listName();
        String violation = (rule.mode() == Br455ListRule.Mode.MUST_EXIST_IN_LIST)
                ? "not RefDataEntry( listName == \"{value}\", value == $v )"
                : "RefDataEntry( listName == \"{value}\", value == $v )";


        String rhs = bind + "\n" + violation;

        // DSL LHS (human key). For now we keep it concrete (demo)
        String lhs = "- " + uk.gov.hmrc.rules.br455.format.Br455ThenMessageFormatter
                .friendlyPathNoDots(rule.fieldPath()) + " " +
                (rule.mode() == Br455ListRule.Mode.MUST_EXIST_IN_LIST
                        ? "must exist in list {value}"
                        : "must not exist in list {value}");


        DslKey key = new DslKey(
                "BR455",
                "condition",
                "SINGLE",
                rootKey(rule.fieldPath()),
                rootKey(rule.fieldPath()),
                stripRoot(rule.fieldPath()),
                rule.mode().name()
        );

        return List.of(new DslEntry(key, "condition", lhs, rhs));
    }



    @Override
    public List<DslEntry> emitThen(RuleModel model) {

        // =========================
        // CHANGED METHOD: emitThen()
        // =========================
        String ifCondition = model.ruleRow().ifCondition();
        Br455ListRule rule = parser.parse(ifCondition);

        String lhs = Br455ThenMessageFormatter.buildThenDslLhs(
                "BR455",
                rule.fieldPath(),
                rule.mode(),
                rule.listName()
        );

        String msg = Br455ThenMessageFormatter.buildThenMessage(
                "BR455",
                rule.fieldPath(),
                rule.mode(),
                rule.listName()
        );

        String rhs = "System.out.println(\"" + escape(msg) + "\");";

        DslKey key = new DslKey(
                "BR455",
                "then",
                "SINGLE",
                rootKey(rule.fieldPath()),
                rootKey(rule.fieldPath()),
                stripRoot(rule.fieldPath()),
                "PRINT"
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
