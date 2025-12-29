package uk.gov.hmrc.rules.ir;

public class Constraint {

    private final String operator;
    private final Object value;

    public Constraint(String operator, Object value) {
        this.operator = operator;
        this.value = value;
    }

    public String getOperator() {
        return operator;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) value;
    }

    @Override
    public String toString() {
        return "Constraint{" +
               "operator='" + operator + '\'' +
               ", value=" + value +
               '}';
    }
}
