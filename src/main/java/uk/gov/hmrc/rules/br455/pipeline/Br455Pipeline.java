package uk.gov.hmrc.rules.br455.pipeline;

import uk.gov.hmrc.rules.br455.dsl.Br455DslEmitter;
import uk.gov.hmrc.rules.br455.dslr.Br455RuleSetDslrEmitter;
import uk.gov.hmrc.rules.br455.parsing.Br455IfParser;
import uk.gov.hmrc.rules.dsl.DslEmission;
import uk.gov.hmrc.rules.dsl.DslEmitter;
import uk.gov.hmrc.rules.dslr.DslrEmitter;
import uk.gov.hmrc.rules.ir.RuleModel;
import uk.gov.hmrc.rules.parsing.ParsedCondition;
import uk.gov.hmrc.rules.parsing.logic.LogicExtractionResult;
import uk.gov.hmrc.rules.parsing.logic.RuleLogicExtractor;
import uk.gov.hmrc.rules.parsing.logic.impl.Br455LogicExtractor;
import uk.gov.hmrc.rules.pipeline.RulePipeline;

import java.util.List;
import java.util.Objects;

public final class Br455Pipeline implements RulePipeline {

    private final RuleLogicExtractor logicExtractor;
    private final Br455IfParser ifParser;
    private final DslEmitter dslEmitter;
    private final DslrEmitter dslrEmitter;

    // Version: 2026-01-25
    private final java.util.Set<String> whenLines = new java.util.LinkedHashSet<>();
    private final java.util.Set<String> thenLines = new java.util.LinkedHashSet<>();
    private final java.util.List<String> dslrRules = new java.util.ArrayList<>();

    // pick a location that suits your project layout
    private final java.nio.file.Path outputDir =
            java.nio.file.Paths.get("src/main/resources/rules/br455");


    public Br455Pipeline() {
        this(
                new Br455LogicExtractor(),
                new Br455IfParser(),
                new DslEmitter(List.of(new Br455DslEmitter())),
                new DslrEmitter(List.of(new Br455RuleSetDslrEmitter()))
        );
    }

    public Br455Pipeline(
            RuleLogicExtractor logicExtractor,
            Br455IfParser ifParser,
            DslEmitter dslEmitter,
            DslrEmitter dslrEmitter
    ) {
        this.ifParser = Objects.requireNonNull(ifParser, "ifParser");
        this.dslEmitter = Objects.requireNonNull(dslEmitter, "dslEmitter");
        this.dslrEmitter = Objects.requireNonNull(dslrEmitter, "dslrEmitter");
        this.logicExtractor = Objects.requireNonNull(logicExtractor, "logicExtractor");
    }

    @Override
    public void process(uk.gov.hmrc.rules.RuleRow row) {

        System.out.println("=== BR455 PIPELINE ===");
        System.out.println("ruleId=" + row.id());

        // 1) RAW input (what Excel actually said)
        System.out.println("rawCol6=" + row.rawCol6());
        System.out.println();

        // 2) Extract logic (BR455 grammar)
        LogicExtractionResult logic = logicExtractor.extract(row.rawCol6());

        System.out.println("extracted.if=" + logic.ifText());
        System.out.println("extracted.msg=" + logic.messageText());
        System.out.println();

        // 3) Parse the extracted IF into ParsedConditions
        List<ParsedCondition> parsed = ifParser.parseParsedConditions(logic.ifText());

        // 4) Build model (option A: keep as-is for now)
        RuleModel model = new RuleModel(row, parsed);

        // If you want the message available downstream, evolve RuleModel later:
        // RuleModel model = new RuleModel(row, parsed, logic.messageText());

        String ruleSet = row.getRuleSet().name();

        DslEmission dslEmission = dslEmitter.emit(ruleSet, model);

        String dslText = dslEmitter.renderDsl(dslEmission);
        System.out.println("DSL:\n" + dslText);

        String dslrText = dslrEmitter.emitDslr(ruleSet, model);
        System.out.println("DSLR:\n" + dslrText);
    }

    // Version: 2026-01-25
    private void addDslLines(String dslText) {
        if (dslText == null || dslText.isBlank()) {
            return;
        }
        for (String raw : dslText.split("\\R")) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("#")) continue;

            if (line.startsWith("[when]")) {
                whenLines.add(line);
            } else if (line.startsWith("[then]")) {
                thenLines.add(line);
            } else {
                // Fail loudly so nothing silently disappears
                throw new IllegalArgumentException("Unexpected DSL line (expected [when]/[then]): " + line);
            }
        }
    }

    // Version: 2026-01-25
    public void finish() throws java.io.IOException {
        java.nio.file.Path dslDir = outputDir.resolve("dsl");
        java.nio.file.Path dslrDir = outputDir.resolve("dslr");

        java.nio.file.Files.createDirectories(dslDir);
        java.nio.file.Files.createDirectories(dslrDir);

        java.util.List<String> whens = new java.util.ArrayList<>(whenLines);
        java.util.List<String> thens = new java.util.ArrayList<>(thenLines);

        java.util.Collections.sort(whens);
        java.util.Collections.sort(thens);

        writeText(dslDir.resolve("whens.dsl"),
                "# Version: 2026-01-25\n" +
                        "# Auto-generated - DO NOT EDIT\n\n" +
                        String.join("\n", whens) + "\n");

        writeText(dslDir.resolve("thens.dsl"),
                "# Version: 2026-01-25\n" +
                        "# Auto-generated - DO NOT EDIT\n\n" +
                        String.join("\n", thens) + "\n");

        writeText(dslrDir.resolve("rules.dslr"),
                "// Version: 2026-01-25\n" +
                        "// Auto-generated - DO NOT EDIT\n\n" +
                        String.join("\n\n", dslrRules) + "\n");
    }

    private static void writeText(java.nio.file.Path file, String content) throws java.io.IOException {
        java.nio.file.Files.writeString(
                file,
                content,
                java.nio.charset.StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
        );
    }


}
