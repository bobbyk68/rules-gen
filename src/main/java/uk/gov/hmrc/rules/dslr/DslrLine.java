package uk.gov.hmrc.rules.dslr;

public class DslrLine {
    private final String text;

    public DslrLine(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }
}
