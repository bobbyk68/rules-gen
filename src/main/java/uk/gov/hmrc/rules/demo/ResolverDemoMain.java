package uk.gov.hmrc.rules.demo;

import uk.gov.hmrc.rules.br455.reflection.NoOpExcelAliasResolver;
import uk.gov.hmrc.rules.br455.reflection.ReflectionScalarPathResolver;
import uk.gov.hmrc.rules.br455.reflection.ResolvedScalarPath;

// Version: 2026-01-23 v1.0.0
public final class ResolverDemoMain {

    public static void main(String[] args) {
        ReflectionScalarPathResolver resolver =
                new ReflectionScalarPathResolver(new NoOpExcelAliasResolver());

        run(resolver, DemoFacts.RootFact.class, "type.code");
        run(resolver, DemoFacts.RootFact.class, "preference.code");
        run(resolver, DemoFacts.RootFact.class, "AdditionalActor.subRole.code");
    }

    private static void run(ReflectionScalarPathResolver resolver, Class<?> root, String excelPath) {
        try {
            ResolvedScalarPath p = resolver.resolveScalar(root, excelPath);
            System.out.println("excel='" + excelPath + "' => drools='" + p.getPath() + "'  reason=" + p.getReason());
        } catch (Exception e) {
            System.out.println("excel='" + excelPath + "' => ERROR: " + e.getMessage());
        }
    }
}
