package uk.gov.hmrc.rules.refdata;

public class RefDataSetFact {
    private final String name;
    private final java.util.Set<String> values;

    public RefDataSetFact(String name, java.util.Set<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() { return name; }
    public java.util.Set<String> getValues() { return values; }
}
