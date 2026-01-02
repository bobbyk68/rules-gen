package uk.gov.hmrc.rules.demo;

import uk.gov.hmrc.rules.ir.RuleModel;

public interface RulesetProfile {
    String rulesetCode();                 // "BR675", "BRxxx"
    HeaderSpec headerSpec();              // which annotations exist
    DslrLayout layout();                  // how many blocks/lines
    List<DslrBlock> buildBlocks(RuleModel model); // 2-line, 4-line, etc.
    String renderAction(RuleModel model);  // THEN section text
}
