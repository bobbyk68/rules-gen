# SnakeYAML Quick Reference

## Key Concepts

### 1. Flow vs Block Style

**BLOCK Style** (Traditional YAML):
```yaml
person:
  name: John
  age: 30
  hobbies:
    - reading
    - coding
```

**FLOW Style** (JSON-like):
```yaml
person: {name: John, age: 30, hobbies: [reading, coding]}
```

### 2. DumperOptions.FlowStyle

Controls the default rendering style:

- `FlowStyle.BLOCK` - All nested structures use block style (newlines, indentation)
- `FlowStyle.FLOW` - All nested structures use inline JSON-like style
- `FlowStyle.AUTO` - SnakeYAML decides (usually not what you want)

### 3. Field Filtering

Three ways to control which fields appear in output:

**A. `transient` keyword (Java built-in)**
```java
public class MyClass {
    private String name;
    private transient String password; // Won't be serialized
}
```

**B. Custom PropertyUtils (SnakeYAML)**
```java
representer.getPropertyUtils().setSkipMissingProperties(true);
representer.setPropertyUtils(new CustomPropertyUtils(excludedFields));
```

**C. Custom Representer (Advanced)**
Override `getProperties()` method to filter dynamically.

### 4. Null Handling

**Include nulls** (default in SnakeYAML):
```yaml
person:
  name: John
  middleName: null  # Explicit null
  age: 30
```

**Skip nulls** (requires custom Representer):
```yaml
person:
  name: John
  age: 30  # middleName omitted
```

In SnakeYAML 1.33, there's no simple flag to skip nulls globally. You need to:
1. Filter properties before dumping, or
2. Use a custom Representer that checks for null values

### 5. Your Data Structure

```
Map<String, Map<String, List<PeriodDto>>>
     │         │         │        │
     │         │         │        └─ Record with startDate, endDate, description
     │         │         │
     │         │         └─ List of periods for each code
     │         │
     │         └─ Map of code → periods
     │
     └─ Map of category name → code map

Example:
{
  "AdditionalInformationTypes": {
    "ACA": [
      {startDate: "2001-01-01T00:00:00", endDate: null, description: "Spec..."}
    ],
    "AES": [...]
  },
  "AirportCodes": {...}
}
```

## Common Scenarios

### Scenario 1: Write for Human Review (Git)
```java
// Use ALL_BLOCK - clean diffs
YamlWriter writer = new YamlWriter(YamlWriter.StyleConfig.ALL_BLOCK);
```

### Scenario 2: Compact Output (File Size Matters)
```java
// Use ALL_FLOW
YamlWriter writer = new YamlWriter(YamlWriter.StyleConfig.ALL_FLOW);
```

### Scenario 3: Balanced (Your Case)
```java
// Use DEFAULT (Option C)
YamlWriter writer = new YamlWriter();
```

### Scenario 4: Hide Internal Fields
```java
YamlWriter.StyleConfig config = YamlWriter.StyleConfig.builder()
    .excludeFields(Set.of("internalId", "metadata"))
    .build();
YamlWriter writer = new YamlWriter(config);
```

## SnakeYAML 1.33 Limitations

1. **No per-type flow style control** - You can't easily say "PeriodDto always flow, but outer maps block" without custom Representers
2. **No simple null skipping** - Requires custom Representer
3. **LocalDateTime handling** - Not built-in, renders as full object by default
4. **Java Records** - Works, but treated as JavaBeans (getters required)

## Upgrading to SnakeYAML 2.x

If you upgrade to SnakeYAML 2.x in the future:
- Constructor API changed (requires `LoaderOptions`)
- Better Java Records support
- Improved type safety
- Some API deprecations

Migration would look like:
```java
// 1.33
Yaml yaml = new Yaml(representer, dumperOptions);

// 2.x
LoaderOptions loaderOptions = new LoaderOptions();
Yaml yaml = new Yaml(new Constructor(loaderOptions), representer, dumperOptions);
```

## Debugging YAML Output

### See what SnakeYAML is doing:
```java
String output = yaml.dump(data);
System.out.println(output);  // Print to see actual output
```

### Check node types:
```java
Node node = yaml.represent(data);
System.out.println("Node type: " + node.getClass());
System.out.println("Tag: " + node.getTag());
```

### Verify round-trip:
```java
String output = yaml.dump(data);
Object reloaded = yaml.load(output);
assert data.equals(reloaded);  // Verify no data loss
```

## Performance Tips

1. **Reuse Yaml instance** - Creating Yaml objects is expensive
   ```java
   private static final Yaml YAML = new Yaml(representer, options);
   ```

2. **Use LinkedHashMap** - Preserves insertion order (matters for output consistency)

3. **Avoid AUTO flow style** - It's slower (SnakeYAML analyzes each structure)

4. **Large files** - Consider streaming API for huge datasets:
   ```java
   yaml.dumpAll(iterator, writer);  // Stream multiple documents
   ```

## Complete Minimal Example

```java
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.util.*;

public class MinimalExample {
    public static void main(String[] args) throws Exception {
        // 1. Create data
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", "John");
        data.put("age", 30);
        data.put("city", null);
        
        // 2. Configure options
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);
        
        // 3. Create YAML writer
        Yaml yaml = new Yaml(options);
        
        // 4. Write to file
        try (FileWriter writer = new FileWriter("output.yml")) {
            yaml.dump(data, writer);
        }
        
        // Or to string
        String output = yaml.dump(data);
        System.out.println(output);
    }
}
```

Output:
```yaml
name: John
age: 30
city: null
```

## Your Implementation Checklist

- [x] PeriodDto record created
- [x] YamlWriter class with StyleConfig
- [x] Support for flow/block styles
- [x] Include nulls (your requirement)
- [x] Field exclusion capability
- [x] Comprehensive unit tests
- [x] Round-trip validation
- [ ] Integrate into YamlReferenceDataProvider
- [ ] Add logging
- [ ] Error handling for file I/O
- [ ] Consider adding schema validation
