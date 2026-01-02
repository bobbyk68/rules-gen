package uk.gov.hmrc.rules.emitter;

import uk.gov.hmrc.rules.demo.RulesetProfile;
import uk.gov.hmrc.rules.ir.RuleModel;
import uk.gov.hmrc.rules.parsing.DomainFieldDescriptor;

import java.util.Set;

class DslEmitter {
    class DslrEmitter {
        private final RulesetProfileRegistry registry;

        String emit(RuleModel model) {
            RulesetProfile profile = registry.forRuleset(model.getId()); // or model.getBrCode()
            return renderUsing(profile, model);
        }
    }

    List<DslSentence> emitVocabulary(Set<DomainFieldDescriptor> fields,
                                     Set<Operator> operators);
}
