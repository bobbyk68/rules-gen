package uk.gov.hmrc.rules.br455.lookup;

public final class PointerResolver {

    private PointerResolver() {
    }

    public static String of(Object fact, Leaf leaf) {
        // TODO: replace with your real registry lookup (class -> base pointer builder)
        String base = "/"; // placeholder
        String suffix = (leaf == null) ? "" : leaf.suffix;
        return base + suffix;
    }
}
