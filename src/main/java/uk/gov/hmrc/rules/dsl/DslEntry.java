package uk.gov.hmrc.rules.dsl;

/**
 * @param section condition / consequence
 * @param lhs     text with {value} placeholders
 * @param rhs     drl expansion
 */
public record DslEntry(DslKey key, String section, String lhs, String rhs) {
}
