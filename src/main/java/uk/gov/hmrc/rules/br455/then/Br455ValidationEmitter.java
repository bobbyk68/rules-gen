package uk.gov.hmrc.rules.br455.then;

import uk.gov.hmrc.rules.br455.leaf.Br455Leaf;

public interface Br455ValidationEmitter {

    /**
     * Emit a typed BR455 validation event.
     *
     * @param ruleSet e.g. "BR455"
     * @param factAlias e.g. "$gi" or "$cs" depending on which fact is bound
     * @param leaf semantic token (e.g. VM_TYPE / TYPE_CODE)
     */
    Object emit(String ruleSet, String factAlias, Br455Leaf leaf);
}
