package uk.gov.hmrc.rules.dslr;

public record DslrLine(String text) {

    @Override
    public String toString() {
        return text;
    }
}
