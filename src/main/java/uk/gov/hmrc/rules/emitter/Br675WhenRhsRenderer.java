package uk.gov.hmrc.rules.emitter;

import uk.gov.hmrc.rules.ir.Constraint;
import uk.gov.hmrc.rules.ir.FactConditionNode;

public class Br675WhenRhsRenderer {

    public String render(FactConditionNode node) {

        String factType = factClass(node.getFactType());
        String alias    = node.getAlias();
        String parent   = node.getParentAlias();

        StringBuilder sb = new StringBuilder();
        sb.append(alias)
          .append(": ")
          .append(factType)
          .append("(\n");

        // standard GI → sequence binding
        sb.append("    sequenceNumber == ")
          .append(parent)
          .append("Seq");

        // field constraints (if any)
        node.getFieldConstraints().forEach((field, constraint) -> {
            sb.append(",\n    ")
              .append(renderConstraint(field, constraint));
        });

        sb.append("\n)");

        return sb.toString();
    }

    private String renderConstraint(String field, Constraint c) {
        String op = c.getOperator();

        if ("IS_PROVIDED".equals(op)) {
            return field + " != null";
        }

        if ("==".equals(op)) {
            return field + " == \"" + c.getValue() + "\"";
        }

        if ("IN".equals(op)) {
            return field + " in (" + String.join(", ", (Iterable<String>) c.getValue()) + ")";
        }

        throw new IllegalStateException("Unsupported operator: " + op);
    }

    private String factClass(String entityType) {
        return switch (entityType) {
            case "GoodsItem" -> "GoodsItemFact";
            case "SpecialProcedure" -> "GoodsItemSpecialProcedureFact";
            default -> throw new IllegalArgumentException("Unknown entityType: " + entityType);
        };
    }
}
