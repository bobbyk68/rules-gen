package uk.gov.hmrc.cars.refdata.yaml;

import org.yaml.snakeyaml.DumperOptions;
import uk.gov.hmrc.cars.refdata.domain.PeriodDto;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Demonstration of different YAML output styles.
 * Run this to see examples of each configuration.
 */
public class YamlWriterDemo {
    
    public static void main(String[] args) {
        Map<String, Map<String, List<PeriodDto>>> sampleData = createSampleData();
        
        System.out.println("=".repeat(80));
        System.out.println("YAML WRITER OUTPUT EXAMPLES");
        System.out.println("=".repeat(80));
        
        // Option A: All Flow
        System.out.println("\n" + "=".repeat(80));
        System.out.println("OPTION A: ALL FLOW (Most Compact)");
        System.out.println("=".repeat(80));
        YamlWriter writerA = new YamlWriter(YamlWriter.StyleConfig.ALL_FLOW);
        System.out.println(writerA.writeToString(sampleData));
        
        // Option B: All Block
        System.out.println("\n" + "=".repeat(80));
        System.out.println("OPTION B: ALL BLOCK (Most Readable)");
        System.out.println("=".repeat(80));
        YamlWriter writerB = new YamlWriter(YamlWriter.StyleConfig.ALL_BLOCK);
        System.out.println(writerB.writeToString(sampleData));
        
        // Option C: Mixed (Default)
        System.out.println("\n" + "=".repeat(80));
        System.out.println("OPTION C: MIXED - Categories Block, Objects Flow (RECOMMENDED)");
        System.out.println("=".repeat(80));
        YamlWriter writerC = new YamlWriter(); // Uses DEFAULT config
        System.out.println(writerC.writeToString(sampleData));
        
        // Custom: Exclude description field
        System.out.println("\n" + "=".repeat(80));
        System.out.println("CUSTOM: Exclude 'description' field");
        System.out.println("=".repeat(80));
        YamlWriter.StyleConfig customConfig = YamlWriter.StyleConfig.builder()
            .defaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
            .includeNulls(true)
            .excludeFields(Set.of("description"))
            .build();
        YamlWriter writerCustom = new YamlWriter(customConfig);
        System.out.println(writerCustom.writeToString(sampleData));
        
        // Show size comparison
        System.out.println("\n" + "=".repeat(80));
        System.out.println("SIZE COMPARISON");
        System.out.println("=".repeat(80));
        System.out.println("All Flow:  " + writerA.writeToString(sampleData).length() + " characters");
        System.out.println("All Block: " + writerB.writeToString(sampleData).length() + " characters");
        System.out.println("Mixed:     " + writerC.writeToString(sampleData).length() + " characters");
        System.out.println("No Desc:   " + writerCustom.writeToString(sampleData).length() + " characters");
    }
    
    private static Map<String, Map<String, List<PeriodDto>>> createSampleData() {
        Map<String, Map<String, List<PeriodDto>>> data = new LinkedHashMap<>();
        
        // AdditionalInformationTypes
        Map<String, List<PeriodDto>> additionalInfoTypes = new LinkedHashMap<>();
        additionalInfoTypes.put("ACA", List.of(
            new PeriodDto(
                LocalDateTime.parse("2001-01-01T00:00:00"),
                null,
                "Specification of the documentary requirements"
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
        
        return data;
    }
}
