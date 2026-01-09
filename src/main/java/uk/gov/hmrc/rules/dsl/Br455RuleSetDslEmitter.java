package uk.gov.hmrc.rules.dsl;

import java.util.ArrayList;
import java.util.List;
import uk.gov.hmrc.rules.br455.Br455IfParser;
import uk.gov.hmrc.rules.br455.Br455ListRule;
import uk.gov.hmrc.rules.br455.Br455RootFactRegistry;
import uk.gov.hmrc.rules.ir.RuleModel;

public final class Br455RuleSetDslEmitter implements RuleSetDslEmitter {

    private final Br455IfParser parser = new Br455IfParser();
    private final Br455RootFactRegistry registry = new Br455RootFactRegistry();

    @Override
    public boolean supports(String ruleSet) {
        if (ruleSet == null) return false;
        return ruleSet.trim().equalsIgnoreCase("BR455");
    }

    @Override
    public List<DslEntry> emitWhen(RuleModel model) {

        // --------- CHANGE HERE ONLY IF YOUR API DIFFERS ----------
        // If your RuleModel doesn't expose the RuleRow, change these 2 lines.
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
                ? "not RefDataEntry( listName == \"" + escape(listName) + "\", value == $v )"
                : "RefDataEntry( listName == \"" + escape(listName) + "\", value == $v )";

        String rhs = bind + "\n" + violation;

        // DSL LHS (human key). For now we keep it concrete (demo)
        String lhs = "- " + stripRoot(rule.fieldPath()) + " " +
                (rule.mode() == Br455ListRule.Mode.MUST_EXIST_IN_LIST ? "must exist in list " : "must not exist in list ")
                + listName;

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

        // --------- CHANGE HERE ONLY IF YOUR API DIFFERS ----------
        String id = model.ruleRow().id();
        String ifCondition = model.ruleRow().ifCondition();
        // ---------------------------------------------------------

        Br455ListRule rule = parser.parse(ifCondition);

        // For demo: print something deterministic. We’ll revisit wording later.
        String fieldTail = stripRoot(rule.fieldPath());
        String list = rule.listName();

        String message = (rule.mode() == Br455ListRule.Mode.MUST_EXIST_IN_LIST)
                ? fieldTail + " does not exist in list " + list
                : fieldTail + " does exist in list " + list;

        String lhs = "Then emit " + id + " {message}";
        String rhs = "System.out.println(\"" + escape(message) + "\");";

        DslKey key = new DslKey(
                "BR455",
                "consequence",
                "SINGLE",
                rootKey(rule.fieldPath()),
                rootKey(rule.fieldPath()),
                fieldTail,
                "PRINT"
        );

        return List.of(new DslEntry(key, "consequence", lhs, rhs));
    }


    private String shortFieldPath(String fullPath) {
        if (fullPath == null) return "";
        int dot = fullPath.indexOf('.');
        return (dot < 0) ? fullPath : fullPath.substring(dot + 1);
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
