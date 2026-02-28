package uk.gov.hmrc.rules.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that loads a real XML response, unmarshals it into
 * RuleFiredValidationResult objects and runs the deduplicator end to end.
 *
 * The XML file is at src/test/resources/validation-response.xml
 * Edit that file to add your own dedupe scenarios.
 */
class XPathDeduplicatorXmlIntegrationTest {

    private XPathDeduplicator deduplicator;

    @BeforeEach
    void setUp() {
        deduplicator = new XPathDeduplicator(Map.of(
                "partyRoleType",         false,
                "countryRegionRoleType", true
        ));
    }

    @Test
    void shouldLoadXmlUnmarshalAndDeduplicate() throws Exception {
        List<RuleFiredValidationResult> results = loadFromXml("validation-response.xml");

        // Verify we loaded results correctly before dedup
        assertThat(results).isNotEmpty();

        int originalSize = results.size();
        long originalPointerCount = results.stream()
                .mapToLong(r -> r.getPointers().size())
                .sum();

        List<RuleFiredValidationResult> deduped = deduplicator.deduplicate(results);

        // Log what happened for visibility when running the test
        System.out.println("Original results: " + originalSize);
        System.out.println("Original pointer count: " + originalPointerCount);
        System.out.println("Deduped results: " + deduped.size());
        System.out.println("Deduped pointer count: " +
                deduped.stream().mapToLong(r -> r.getPointers().size()).sum());

        // Verify output is never larger than input
        assertThat(deduped.size()).isLessThanOrEqualTo(originalSize);

        // Verify order is preserved - rule IDs should appear in same relative order
        List<String> dedupedRuleIds = deduped.stream()
                .map(r -> r.getRule().getId())
                .toList();

        List<String> originalRuleIds = new ArrayList<>(results.stream()
                .map(r -> r.getRule().getId())
                .toList());

        // Every rule ID in deduped output should appear in the same order as original
        // (originalRuleIds may have more entries if some were fully deduped away)
        assertThat(dedupedRuleIds).containsSubsequenceOf(originalRuleIds);

        // Verify no result in the output has an empty pointers list
        assertThat(deduped).allSatisfy(result ->
                assertThat(result.getPointers()).isNotEmpty()
        );

        // Print each result for inspection
        deduped.forEach(result -> {
            System.out.println("Rule: " + result.getRule().getId());
            result.getPointers().forEach(p -> System.out.println("  -> " + p));
        });
    }

    // -------------------------------------------------------------------------
    // XML unmarshalling
    // -------------------------------------------------------------------------

    /**
     * Loads validation results from an XML file on the classpath.
     * Parses ns4:validationResults elements and maps them to RuleFiredValidationResult.
     *
     * TODO: Replace this with your existing JAXB/unmarshalling infrastructure if available.
     */
    private List<RuleFiredValidationResult> loadFromXml(String classpathResource) throws Exception {
        InputStream xmlStream = getClass().getClassLoader().getResourceAsStream(classpathResource);
        assertThat(xmlStream).as("XML resource not found: " + classpathResource).isNotNull();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlStream);

        // ns4:validationResults elements
        NodeList validationResultNodes = document.getElementsByTagNameNS(
                "http://cmm.core.ecf/BaseTypes/cmmValidationTypes/trade/2017/02/22/",
                "validationResults"
        );

        List<RuleFiredValidationResult> results = new ArrayList<>();

        for (int i = 0; i < validationResultNodes.getLength(); i++) {
            Element validationResultElement = (Element) validationResultNodes.item(i);
            results.add(unmarshalValidationResult(validationResultElement));
        }

        return results;
    }

    /**
     * Unmarshals a single ns4:validationResults element into a RuleFiredValidationResult.
     *
     * Mapped fields:
     * - ns3:pointers  -> pointers list (can be multiple)
     * - ns3:id inside ns4:rule -> rule ID
     * - ns3:type -> type
     * - ns3:qualifier -> qualifier
     *
     * TODO: Extend this to map information, goodsItemSequenceNumber etc. as needed.
     */
    private RuleFiredValidationResult unmarshalValidationResult(Element element) {
        String ns3 = "http://cmm.core.ecf/BaseTypes/cmmServiceTypes/trade/2017/02/22/";
        String ns4 = "http://cmm.core.ecf/BaseTypes/cmmValidationTypes/trade/2017/02/22/";

        // Extract all ns3:pointers - there can be multiple per validationResults element
        NodeList pointerNodes = element.getElementsByTagNameNS(ns3, "pointers");
        List<String> pointers = new ArrayList<>();
        for (int i = 0; i < pointerNodes.getLength(); i++) {
            pointers.add(pointerNodes.item(i).getTextContent().trim());
        }

        // Extract rule ID from ns4:rule > ns3:id
        String ruleId = null;
        NodeList ruleNodes = element.getElementsByTagNameNS(ns4, "rule");
        if (ruleNodes.getLength() > 0) {
            Element ruleElement = (Element) ruleNodes.item(0);
            NodeList idNodes = ruleElement.getElementsByTagNameNS(ns3, "id");
            if (idNodes.getLength() > 0) {
                ruleId = idNodes.item(0).getTextContent().trim();
            }
        }

        RuleFiredValidationResult result = new RuleFiredValidationResult();
        result.setPointers(pointers);
        result.setRule(new ValidationRule(ruleId));

        return result;
    }
}
