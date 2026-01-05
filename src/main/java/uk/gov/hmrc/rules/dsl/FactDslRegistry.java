package uk.gov.hmrc.rules.dsl;

public interface FactDslRegistry {

    Resolved resolve(uk.gov.hmrc.rules.ir.FactConditionNode fc);

    record Resolved(
            AnchorSpec anchor,
            DashSpec dash
    ) {
    }

    record AnchorSpec(
            String dslAnchorLhs,
            String dslAnchorRhs,

            String alias,       // "$sp"
            String factClass,   // "GoodsItemSpecialProcedureTypeFact"
            String seqVar,      // "$giSeq"

            String bindField,   // used with ":"  (binder)
            String joinField    // used with "==" (negated/joined child)
    ) {
    }

    record DashSpec(
            String dslDashLhs,
            String dslDashRhs
    ) {
    }
}
