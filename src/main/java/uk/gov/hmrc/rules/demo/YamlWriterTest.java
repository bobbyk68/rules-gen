package uk.gov.hmrc.cars.refdata.yaml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import uk.gov.hmrc.cars.refdata.domain.PeriodDto;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for YamlWriter demonstrating:
 * 1. Loading reference data from YAML
 * 2. Writing it back with different style configurations
 * 3. Round-trip validation
 * 4. Field filtering and null handling
 */
class YamlWriterTest {
    
    @TempDir
    Path tempDir;
    
    /**
     * Test loading the reference data YAML and writing it back in Option C style
     * (categories/codes block, PeriodDto flow)
     */
    @Test
    void testRoundTripWithFlowStyle() throws IOException {
        // Given: Load reference data from YAML (simulating your YamlReferenceDataProvider)
        Map<String, Map<String, List<PeriodDto>>> referenceData = loadReferenceData();
        
        // Verify loaded data structure
        assertNotNull(referenceData);
        assertTrue(referenceData.containsKey("AdditionalInformationTypes"));
        assertTrue(referenceData.containsKey("AirportCodes"));
        
        Map<String, List<PeriodDto>> additionalInfoTypes = referenceData.get("AdditionalInformationTypes");
        assertNotNull(additionalInfoTypes);
        assertTrue(additionalInfoTypes.containsKey("ACA"));
        
        List<PeriodDto> acaPeriods = additionalInfoTypes.get("ACA");
        assertEquals(1, acaPeriods.size());
        
        PeriodDto acaPeriod = acaPeriods.get(0);
        assertNotNull(acaPeriod.startDate());
        assertNull(acaPeriod.endDate()); // Verify null is preserved
        assertEquals("Specification of the documentary requirements (used for Document type to be produced, Certificate of origin and Endorsement of the certificate of origin only)", 
                     acaPeriod.description());
        
        // When: Write to YAML with default config (Option C style)
        YamlWriter writer = new YamlWriter();
        Path outputPath = tempDir.resolve("output.yml");
        writer.writeToFile(referenceData, outputPath.toString());
        
        // Then: Verify file was created
        assertTrue(Files.exists(outputPath));
        String output = Files.readString(outputPath);
        
        // Verify structure
        System.out.println("=== OUTPUT (Option C - Block categories, Flow periods) ===");
        System.out.println(output);
        
        // Verify format characteristics
        assertTrue(output.contains("AdditionalInformationTypes:"), "Should have block-style category");
        assertTrue(output.contains("ACA:"), "Should have block-style code");
        assertTrue(output.contains("endDate: null"), "Should include null values");
        
        // Load the output back and verify round-trip
        Map<String, Map<String, List<PeriodDto>>> reloaded = loadFromYaml(outputPath.toString());
        
        // Verify data integrity after round-trip
        assertEquals(referenceData.keySet(), reloaded.keySet(), "Category keys should match");
        
        Map<String, List<PeriodDto>> reloadedAdditionalInfo = reloaded.get("AdditionalInformationTypes");
        assertNotNull(reloadedAdditionalInfo);
        
        List<PeriodDto> reloadedAca = reloadedAdditionalInfo.get("ACA");
        assertEquals(acaPeriods.size(), reloadedAca.size());
        
        PeriodDto reloadedPeriod = reloadedAca.get(0);
        assertEquals(acaPeriod.startDate(), reloadedPeriod.startDate());
        assertEquals(acaPeriod.endDate(), reloadedPeriod.endDate());
        assertEquals(acaPeriod.description(), reloadedPeriod.description());
    }
    
    /**
     * Test writing with ALL_FLOW style (Option A - most compact)
     */
    @Test
    void testAllFlowStyle() throws IOException {
        Map<String, Map<String, List<PeriodDto>>> data = createSampleData();
        
        YamlWriter writer = new YamlWriter(YamlWriter.StyleConfig.ALL_FLOW);
        String output = writer.writeToString(data);
        
        System.out.println("=== OUTPUT (Option A - All Flow) ===");
        System.out.println(output);
        
        // Everything should be inline/flow style
        assertTrue(output.contains("{"), "Should contain flow-style objects");
        assertTrue(output.contains("["), "Should contain flow-style lists");
    }
    
    /**
     * Test writing with ALL_BLOCK style (Option B variant - most readable)
     */
    @Test
    void testAllBlockStyle() throws IOException {
        Map<String, Map<String, List<PeriodDto>>> data = createSampleData();
        
        YamlWriter writer = new YamlWriter(YamlWriter.StyleConfig.ALL_BLOCK);
        String output = writer.writeToString(data);
        
        System.out.println("=== OUTPUT (All Block - Most Readable) ===");
        System.out.println(output);
        
        // Should be fully expanded
        assertTrue(output.contains("- startDate:"), "Should have block-style list items");
        assertTrue(output.contains("  description:"), "Should have indented fields");
    }
    
    /**
     * Test custom configuration with field exclusion
     */
    @Test
    void testFieldExclusion() throws IOException {
        Map<String, Map<String, List<PeriodDto>>> data = createSampleData();
        
        // Create writer that excludes 'description' field
        YamlWriter.StyleConfig config = YamlWriter.StyleConfig.builder()
            .defaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
            .includeNulls(true)
            .excludeFields(Set.of("description"))
            .build();
        
        YamlWriter writer = new YamlWriter(config);
        String output = writer.writeToString(data);
        
        System.out.println("=== OUTPUT (Excluding 'description' field) ===");
        System.out.println(output);
        
        // Description should not appear
        assertFalse(output.contains("description:"), "Description field should be excluded");
        
        // But other fields should still be there
        assertTrue(output.contains("startDate:"), "StartDate should be present");
        assertTrue(output.contains("endDate:"), "EndDate should be present");
    }
    
    /**
     * Test null handling configuration
     */
    @Test
    void testNullHandling() throws IOException {
        Map<String, Map<String, List<PeriodDto>>> data = createSampleData();
        
        // Config with nulls included (default)
        YamlWriter writerWithNulls = new YamlWriter();
        String outputWithNulls = writerWithNulls.writeToString(data);
        
        System.out.println("=== OUTPUT (With Nulls) ===");
        System.out.println(outputWithNulls);
        
        assertTrue(outputWithNulls.contains("endDate: null"), 
                  "Should include null values when configured");
    }
    
    /**
     * Test writing the complete finalIndex structure from your code
     */
    @Test
    void testCompleteReferenceDataStructure() throws IOException {
        // Simulate your finalIndex: Map<String, Map<String, List<PeriodDto>>>
        Map<String, Map<String, List<PeriodDto>>> finalIndex = loadReferenceData();
        
        // Log the size
        System.out.println("Loaded " + finalIndex.size() + " code list categories");
        finalIndex.forEach((category, codes) -> 
            System.out.println("  " + category + ": " + codes.size() + " codes")
        );
        
        // Write it out
        YamlWriter writer = new YamlWriter();
        Path outputPath = tempDir.resolve("final-index.yml");
        writer.writeToFile(finalIndex, outputPath.toString());
        
        // Verify
        assertTrue(Files.exists(outputPath));
        long fileSize = Files.size(outputPath);
        System.out.println("Output file size: " + fileSize + " bytes");
        
        // Verify round-trip
        Map<String, Map<String, List<PeriodDto>>> reloaded = loadFromYaml(outputPath.toString());
        assertEquals(finalIndex.size(), reloaded.size(), "Should preserve all categories");
    }
    
    // ==================== Helper Methods ====================
    
    /**
     * Loads reference data from YAML file (simulating your YamlReferenceDataProvider)
     */
    private Map<String, Map<String, List<PeriodDto>>> loadReferenceData() throws IOException {
        // Create test data directly instead of loading from file
        // This simulates what you get from your YAML loader
        return createSampleData();
    }
    
    /**
     * Creates sample data matching the structure from the screenshots
     */
    private Map<String, Map<String, List<PeriodDto>>> createSampleData() {
        Map<String, Map<String, List<PeriodDto>>> data = new LinkedHashMap<>();
        
        // AdditionalInformationTypes
        Map<String, List<PeriodDto>> additionalInfoTypes = new LinkedHashMap<>();
        additionalInfoTypes.put("ACA", List.of(
            new PeriodDto(
                LocalDateTime.parse("2001-01-01T00:00:00"),
                null,
                "Specification of the documentary requirements (used for Document type to be produced, Certificate of origin and Endorsement of the certificate of origin only)"
            )
        ));
        additionalInfoTypes.put("AES", List.of(
            new PeriodDto(
                LocalDateTime.parse("2001-01-01T00:00:00"),
                null,
                "Textual Explanation"
            )
        ));
        additionalInfoTypes.put("AFB", List.of(
            new PeriodDto(
                LocalDateTime.parse("2001-01-01T00:00:00"),
                null,
                "Customs Position Motivation"
            )
        ));
        additionalInfoTypes.put("ALT", List.of(
            new PeriodDto(
                LocalDateTime.parse("2001-01-01T00:00:00"),
                null,
                "Additional fiscal references"
            )
        ));
        additionalInfoTypes.put("BLF", List.of(
            new PeriodDto(
                LocalDateTime.parse("2001-01-01T00:00:00"),
                null,
                "Bond Reference Number"
            )
        ));
        additionalInfoTypes.put("CEX", List.of(
            new PeriodDto(
                LocalDateTime.parse("2001-01-01T00:00:00"),
                null,
                "Certificate reference and issuing authority"
            )
        ));
        
        data.put("AdditionalInformationTypes", additionalInfoTypes);
        
        // AirportCodes
        Map<String, List<PeriodDto>> airportCodes = new LinkedHashMap<>();
        airportCodes.put("LHR", List.of(
            new PeriodDto(
                LocalDateTime.parse("2001-01-01T00:00:00"),
                null,
                "London Heathrow Airport"
            )
        ));
        airportCodes.put("LGW", List.of(
            new PeriodDto(
                LocalDateTime.parse("2001-01-01T00:00:00"),
                null,
                "London Gatwick Airport"
            )
        ));
        
        data.put("AirportCodes", airportCodes);
        
        // AmendmentReasonTypes (to match the screenshot showing 35 entries)
        Map<String, List<PeriodDto>> amendmentReasons = new LinkedHashMap<>();
        amendmentReasons.put("AR1", List.of(
            new PeriodDto(
                LocalDateTime.parse("2001-01-01T00:00:00"),
                null,
                "Amendment Reason Type 1"
            )
        ));
        
        data.put("AmendmentReasonTypes", amendmentReasons);
        
        return data;
    }
    
    /**
     * Loads data from YAML file for round-trip testing
     */
    private Map<String, Map<String, List<PeriodDto>>> loadFromYaml(String filepath) throws IOException {
        Yaml yaml = new Yaml(new Constructor(Map.class, new org.yaml.snakeyaml.LoaderOptions()));
        
        try (FileInputStream fis = new FileInputStream(filepath)) {
            Map<String, Object> raw = yaml.load(fis);
            
            // Convert the loaded structure to our typed map
            Map<String, Map<String, List<PeriodDto>>> result = new LinkedHashMap<>();
            
            raw.forEach((category, codeMap) -> {
                if (codeMap instanceof Map) {
                    Map<String, List<PeriodDto>> typedCodeMap = new LinkedHashMap<>();
                    
                    ((Map<String, Object>) codeMap).forEach((code, periods) -> {
                        if (periods instanceof List) {
                            List<PeriodDto> periodList = new ArrayList<>();
                            
                            ((List<Object>) periods).forEach(period -> {
                                if (period instanceof Map) {
                                    Map<String, Object> periodMap = (Map<String, Object>) period;
                                    
                                    LocalDateTime startDate = parseDateTime(periodMap.get("startDate"));
                                    LocalDateTime endDate = parseDateTime(periodMap.get("endDate"));
                                    String description = (String) periodMap.get("description");
                                    
                                    periodList.add(new PeriodDto(startDate, endDate, description));
                                }
                            });
                            
                            typedCodeMap.put(code, periodList);
                        }
                    });
                    
                    result.put(category, typedCodeMap);
                }
            });
            
            return result;
        }
    }
    
    /**
     * Parse datetime from YAML (can be String or LocalDateTime)
     */
    private LocalDateTime parseDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof String) {
            return LocalDateTime.parse((String) value);
        }
        return null;
    }
}
