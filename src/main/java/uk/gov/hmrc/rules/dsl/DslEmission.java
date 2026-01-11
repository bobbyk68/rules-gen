package uk.gov.hmrc.rules.dsl;

import java.util.List;

public record DslEmission(List<DslEntry> whenEntries, List<DslEntry> thenEntries) {

    public DslEmission(List<DslEntry> whenEntries,
                       List<DslEntry> thenEntries) {
        this.whenEntries = List.copyOf(whenEntries);
        this.thenEntries = List.copyOf(thenEntries);
    }
}

