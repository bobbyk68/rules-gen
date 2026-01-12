package uk.gov.hmrc.rules.br455.then;

import uk.gov.hmrc.rules.br455.leaf.Br455Leaf;
import uk.gov.hmrc.rules.br455.lookup.Br455LeafResolver;

public final class Br455ThenRhsBuilder {

    private final Br455LeafResolver leafResolver;

    public Br455ThenRhsBuilder(Br455LeafResolver leafResolver) {
        this.leafResolver = java.util.Objects.requireNonNull(leafResolver, "leafResolver");
    }

    public String buildDslRhs(String ruleSet, String rootAlias, String fieldPath, String listName) {

        Br455Leaf leaf = leafResolver.resolve(fieldPath);

        // Produces something like:
        // insert(emitter.emit("BR455", of($gi, uk.gov.hmrc.rules.br455.leaf.Br455Leaf.VM_TYPE)));
        return "insert(emitter.emit(drools,"
                + escape(ruleSet)
                + ", of("
                + rootAlias
                + ", "
                + leaf.name()
                + ", "
                + listName
                + ")));";
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
