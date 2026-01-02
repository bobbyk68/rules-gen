package uk.gov.hmrc.rules.emitter;

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
