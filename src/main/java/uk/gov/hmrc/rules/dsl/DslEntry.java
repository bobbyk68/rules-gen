package uk.gov.hmrc.rules.dsl;

    public class DslEntry {
        private final String section;     // condition / consequence
        private final String lhs;         // text with {value} placeholders
        private final String rhs;         // drl expansion
        private final DslKey key;

        public DslEntry(DslKey key, String section, String lhs, String rhs) {
            this.key = key;
            this.section = section;
            this.lhs = lhs;
            this.rhs = rhs;
        }

        public DslKey getKey() { return key; }
        public String getSection() { return section; }
        public String getLhs() { return lhs; }
        public String getRhs() { return rhs; }
    }
