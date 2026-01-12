package uk.gov.hmrc.rules.br455.leaf;

public enum Br455Leaf {

    // Generic
    CODE,
    VALUE,
    TYPE_CODE,

    // BR455-specific semantic leaves (examples you mentioned)
    VM_TYPE,
    UNIT_TYPE_CODE,

    // Safe fallback for DEMO mode
    UNKNOWN
}
