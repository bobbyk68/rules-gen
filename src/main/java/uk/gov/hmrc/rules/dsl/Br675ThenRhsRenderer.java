package uk.gov.hmrc.rules.dsl;

import uk.gov.hmrc.rules.ir.EmitErrorActionNode;

public class Br675ThenRhsRenderer {

    public String renderEmit(uk.gov.hmrc.rules.ir.EmitErrorActionNode emit) {
        String brCode = safe(emit.getBrCode());

        // Keep output stable; message stays placeholder-driven at DSL level.
        // (Once DSL -> DSLR is final, this becomes a proper action.)
        return "System.out.println(\"" + escapeJava("EMIT " + brCode + " {message}") + "\");";
    }

    private static String safe(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static String escapeJava(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
