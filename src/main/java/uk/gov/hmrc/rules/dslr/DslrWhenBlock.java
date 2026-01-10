package uk.gov.hmrc.rules.dslr;

import java.util.ArrayList;
import java.util.List;

public class DslrWhenBlock {
    private final String headline;
    private final List<DslrLine> lines = new ArrayList<>();

    public DslrWhenBlock(String headline) {
        this.headline = headline;
    }

    public String headline() {
        return headline;
    }

    public List<DslrLine> lines() {
        return lines;
    }

    public void addLine(String text) {
        lines.add(new DslrLine(text));
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append(headline).append("\n");
        for (DslrLine l : lines) {
            sb.append(l.text()).append("\n");
        }
        return sb.toString();
    }
}
