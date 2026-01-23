package uk.gov.hmrc.rules.demo;

// Version: 2026-01-23 v1.0.0
public final class DemoFacts {

    private DemoFacts() {
    }

    public static final class RootFact {
        private AdditionalActor additionalActor;
        private String typeCode;              // flattened scalar
        private String preferenceCode;        // flattened scalar

        public AdditionalActor getAdditionalActor() { return additionalActor; }
        public String getTypeCode() { return typeCode; }
        public String getPreferenceCode() { return preferenceCode; }
    }

    public static final class AdditionalActor {
        private SubRole subRole;
        private String subRoleCode;           // flattened scalar on parent

        public SubRole getSubRole() { return subRole; }
        public String getSubRoleCode() { return subRoleCode; }
    }

    public static final class SubRole {
        private String description;           // note: NO "code" field

        public String getDescription() { return description; }
    }
}
