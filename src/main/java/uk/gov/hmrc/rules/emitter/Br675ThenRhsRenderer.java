package uk.gov.hmrc.rules.emitter;

import uk.gov.hmrc.rules.ir.EmitErrorActionNode;

public class Br675ThenRhsRenderer {

    public String render(EmitErrorActionNode action) {

        return new StringBuilder()
            .append("insert(emit(drools, ")
            .append(action.getBrCode())
            .append(", ")
            .append(action.getDmsErrorCode())
            .append("))")
            .toString();
    }
}
