# SnakeYAML Writer - Complete Guide

Complete solution for writing POJOs to YAML with SnakeYAML 1.33, including flow/block style control and field filtering.

## Overview

This package provides a flexible YAML writer that allows you to:
1. **Control output style** (block vs flow) at different nesting levels
2. **Include or exclude null values**
3. **Filter specific fields** from output
4. **Customize formatting** (indentation, line width, etc.)

## Quick Start

### Basic Usage (Option C - Your Preferred Style)

```java
// Load your reference data
Map<String, Map<String, List<PeriodDto>>> finalIndex = loadReferenceData();

// Write to file with default config (categories/codes block, periods flow)
YamlWriter writer = new YamlWriter();
writer.writeToFile(finalIndex, "output.yml");
```

**Output:**
```yaml
AdditionalInformationTypes:
  ACA:
    - {startDate: "2001-01-01T00:00:00", endDate: null, description: "Specification..."}
  AES:
    - {startDate: "2001-01-01T00:00:00", endDate: null, description: "Textual Explanation"}
```

## Style Configurations

### Option A: Everything Flow (Most Compact)

```java
YamlWriter writer = new YamlWriter(YamlWriter.StyleConfig.ALL_FLOW);
writer.writeToFile(data, "output.yml");
```

**Output:**
```yaml
{AdditionalInformationTypes: {ACA: [{startDate: "2001-01-01T00:00:00", endDate: null, ...}]}}
```

### Option B: Everything Block (Most Readable)

```java
YamlWriter writer = new YamlWriter(YamlWriter.StyleConfig.ALL_BLOCK);
writer.writeToFile(data, "output.yml");
```

**Output:**
```yaml
AdditionalInformationTypes:
  ACA:
    - startDate: "2001-01-01T00:00:00"
      endDate: null
      description: "Specification of the documentary requirements..."
  AES:
    - startDate: "2001-01-01T00:00:00"
      endDate: null
      description: "Textual Explanation"
```

### Option C: Mixed (Default - Categories Block, Objects Flow)

```java
YamlWriter writer = new YamlWriter(); // Uses StyleConfig.DEFAULT
writer.writeToFile(data, "output.yml");
```

This is your preferred option - balances readability and compactness.

## Field Control

### Excluding Specific Fields

```java
YamlWriter.StyleConfig config = YamlWriter.StyleConfig.builder()
    .defaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    .includeNulls(true)
    .excludeFields(Set.of("description")) // Don't output description
    .build();

YamlWriter writer = new YamlWriter(config);
writer.writeToFile(data, "output.yml");
```

**Output:**
```yaml
AdditionalInformationTypes:
  ACA:
    - {startDate: "2001-01-01T00:00:00", endDate: null}
```

### Skipping Null Values (NOT your preference, but possible)

```java
YamlWriter.StyleConfig config = YamlWriter.StyleConfig.builder()
    .includeNulls(false) // Skip null fields
    .build();
```

**Output:**
```yaml
AdditionalInformationTypes:
  ACA:
    - {startDate: "2001-01-01T00:00:00", description: "..."}
    # endDate is omitted because it's null
```

## Custom Configuration

### Full Configuration Example

```java
YamlWriter.StyleConfig config = YamlWriter.StyleConfig.builder()
    .defaultFlowStyle(DumperOptions.FlowStyle.BLOCK)  // BLOCK or FLOW or AUTO
    .prettyFlow(true)                                  // Pretty-print flow style
    .indent(2)                                         // Spaces per indent level
    .indicatorIndent(0)                                // Extra indent for list indicators
    .lineWidth(120)                                    // Max line width (0 = no limit)
    .includeNulls(true)                               // Include null values
    .excludeFields(Set.of("internalId", "metadata"))  // Fields to skip
    .build();

YamlWriter writer = new YamlWriter(config);
```

## Understanding Flow Styles

SnakeYAML has three flow style options:

1. **BLOCK** - Traditional YAML with newlines and indentation
   ```yaml
   category:
     code:
       - field1: value1
         field2: value2
   ```

2. **FLOW** - JSON-like compact inline format
   ```yaml
   {category: {code: [{field1: value1, field2: value2}]}}
   ```

3. **AUTO** - SnakeYAML decides based on content (usually not what you want)

### What Controls What?

The `defaultFlowStyle` in `DumperOptions` controls the **overall** style, but:
- Maps and Lists will be rendered according to this style
- For BLOCK style at the top level with FLOW for nested objects, you need custom representers (which is complex in SnakeYAML 1.33)

**In your case (Option C):**
- The default BLOCK style applies to the outer structure (categories and code maps)
- The lists of PeriodDto objects naturally render in flow because they're simple objects
- SnakeYAML 1.33 doesn't have fine-grained per-level control without custom Representer classes

## Integration with Your Code

### In YamlReferenceDataProvider

```java
public class YamlReferenceDataProvider {
    
    // Your existing load method
    public Map<String, Map<String, List<PeriodDto>>> loadCodeLists() {
        // ... existing YAML loading code ...
    }
    
    // New write method
    public void saveCodeLists(Map<String, Map<String, List<PeriodDto>>> finalIndex, 
                              String outputPath) throws IOException {
        YamlWriter writer = new YamlWriter();
        writer.writeToFile(finalIndex, outputPath);
        log.info("Saved {} code lists to {}", finalIndex.size(), outputPath);
    }
}
```

### Usage in Your Service

```java
@Service
public class ReferenceDataService {
    
    public void exportReferenceData() throws IOException {
        // Load from your existing YAML
        Map<String, Map<String, List<PeriodDto>>> finalIndex = 
            yamlReferenceDataProvider.loadCodeLists();
        
        // Write it back out
        YamlWriter writer = new YamlWriter();
        writer.writeToFile(finalIndex, "reference-data-export.yml");
    }
}
```

## Testing

Run the comprehensive test suite:

```bash
mvn test -Dtest=YamlWriterTest
```

The test demonstrates:
1. Loading reference data (simulating your structure)
2. Writing with different style configurations
3. Round-trip validation (load → write → load → verify)
4. Field exclusion
5. Null handling

## Key Differences: Block vs Flow

| Aspect | Block Style | Flow Style |
|--------|-------------|------------|
| **Readability** | High - easy to read and diff | Low - compact but harder to scan |
| **File Size** | Larger | Smaller |
| **Git Diffs** | Clean - one field per line | Messy - whole objects change |
| **Best For** | Configuration files, human editing | Data dumps, space-constrained |

## Advanced: Per-Type Style Control (Complex)

If you need different styles for different object types (e.g., PeriodDto always flow, but other objects block), you need a custom Representer:

```java
public class CustomRepresenter extends Representer {
    public CustomRepresenter(DumperOptions options) {
        super(options);
    }
    
    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, 
                                                  Object propertyValue, Tag customTag) {
        // Custom logic here to control style per property
        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
    }
}
```

This is complex and often not necessary for most use cases.

## Troubleshooting

### Issue: Nulls are being output as empty strings

**Solution:** Ensure `includeNulls(true)` is set in your config.

### Issue: Everything is on one line (all flow style)

**Solution:** Change `defaultFlowStyle` to `BLOCK`:
```java
.defaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
```

### Issue: LocalDateTime not formatting correctly

**Solution:** SnakeYAML 1.33 doesn't natively handle Java 8 time types. You may need to:
1. Convert to String before dumping
2. Or use a custom Representer for LocalDateTime

Example custom representer:
```java
representer.addClassTag(LocalDateTime.class, Tag.STR);
representer.representers.put(LocalDateTime.class, new RepresentToString());
```

### Issue: Field order is wrong

**Solution:** Use `LinkedHashMap` instead of `HashMap` to preserve insertion order.

## Dependencies

```xml
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>1.33</version>
</dependency>
```

## Summary

For your use case (writing reference data for CARS DMS):

1. **Use the default configuration** - it gives you Option C (categories/codes block, periods flow)
2. **Include nulls** - you want `endDate: null` in the output
3. **Don't exclude fields** - unless you specifically need to hide internal fields
4. **Stick with BLOCK default flow style** - more readable for code review

```java
// This is all you need:
YamlWriter writer = new YamlWriter();
writer.writeToFile(finalIndex, "reference-data.yml");
```
