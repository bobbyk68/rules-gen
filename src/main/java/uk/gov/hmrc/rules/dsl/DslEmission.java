package uk.gov.hmrc.rules.dsl;

import java.util.List;

public class DslEmission {

    private final java.util.List<DslEntry> whenEntries;
    private final java.util.List<DslEntry> thenEntries;

    public DslEmission(java.util.List<DslEntry> whenEntries,
                       java.util.List<DslEntry> thenEntries) {
        this.whenEntries = java.util.List.copyOf(whenEntries);
        this.thenEntries = java.util.List.copyOf(thenEntries);
    }

    public java.util.List<DslEntry> whenEntries() {
        return whenEntries;
    }

    public java.util.List<DslEntry> thenEntries() {
        return thenEntries;
    }
}

