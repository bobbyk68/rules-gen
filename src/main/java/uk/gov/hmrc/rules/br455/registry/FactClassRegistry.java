package uk.gov.hmrc.rules.br455.registry;

public final class FactClassRegistry {

    // Version: 2026-01-23 v1.0.0

    private final java.util.Map<String, String> factKeyToFqcn;

    public FactClassRegistry(java.util.Map<String, String> factKeyToFqcn) {
        this.factKeyToFqcn = java.util.Map.copyOf(factKeyToFqcn);
    }

    public Class<?> resolveFactClass(String factKeyOrFqcn) {
        if (factKeyOrFqcn == null || factKeyOrFqcn.isBlank()) {
            throw new IllegalArgumentException("factKeyOrFqcn must not be blank");
        }

        String fqcn = factKeyToFqcn.getOrDefault(factKeyOrFqcn, factKeyOrFqcn);

        try {
            return Class.forName(fqcn, false, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot load class for '" + factKeyOrFqcn + "'. Looked up FQCN='" + fqcn + "'.", e);
        }
    }
}
