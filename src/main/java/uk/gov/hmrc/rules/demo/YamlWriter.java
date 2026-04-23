package uk.gov.hmrc.cars.refdata.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.introspector.Property;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * YAML writer with configurable flow/block styles and field filtering.
 * 
 * Key features:
 * - Control block vs flow style at different nesting levels
 * - Include/exclude null values
 * - Skip specific fields by name
 * - Custom property filtering
 */
public class YamlWriter {
    
    private final Yaml yaml;
    private final DumperOptions options;
    
    /**
     * Creates a YamlWriter with default configuration:
     * - Categories and codes in BLOCK style
     * - PeriodDto objects in FLOW style
     * - Nulls included
     */
    public YamlWriter() {
        this(StyleConfig.DEFAULT);
    }
    
    /**
     * Creates a YamlWriter with custom style configuration.
     */
    public YamlWriter(StyleConfig config) {
        this.options = createDumperOptions(config);
        Representer representer = createRepresenter(config);
        this.yaml = new Yaml(representer, options);
    }
    
    /**
     * Writes the data structure to a file.
     * 
     * @param data The map to write (Map<String, Map<String, List<PeriodDto>>>)
     * @param filepath Path to output file
     */
    public void writeToFile(Object data, String filepath) throws IOException {
        try (Writer writer = new FileWriter(filepath)) {
            yaml.dump(data, writer);
        }
    }
    
    /**
     * Writes the data structure to a string.
     */
    public String writeToString(Object data) {
        return yaml.dump(data);
    }
    
    /**
     * Creates DumperOptions based on configuration.
     */
    private DumperOptions createDumperOptions(StyleConfig config) {
        DumperOptions opts = new DumperOptions();
        
        // Set default flow style
        opts.setDefaultFlowStyle(config.defaultFlowStyle);
        
        // Pretty print configuration
        opts.setPrettyFlow(config.prettyFlow);
        opts.setIndent(config.indent);
        opts.setIndicatorIndent(config.indicatorIndent);
        
        // Line width (0 = no line breaking)
        opts.setWidth(config.lineWidth);
        
        // Don't use explicit start/end document markers (---, ...)
        opts.setExplicitStart(false);
        opts.setExplicitEnd(false);
        
        return opts;
    }
    
    /**
     * Creates a custom Representer for field filtering and null handling.
     */
    private Representer createRepresenter(StyleConfig config) {
        Representer representer = new Representer(options);
        
        // Control null representation
        if (!config.includeNulls) {
            representer.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            representer.getPropertyUtils().setSkipMissingProperties(true);
        }
        
        // Skip specific fields if configured
        if (config.excludedFields != null && !config.excludedFields.isEmpty()) {
            representer.getPropertyUtils().setSkipMissingProperties(true);
            
            // Override property filtering
            representer.setPropertyUtils(new CustomPropertyUtils(config.excludedFields));
        }
        
        return representer;
    }
    
    /**
     * Configuration for YAML output styling.
     */
    public static class StyleConfig {
        DumperOptions.FlowStyle defaultFlowStyle;
        boolean prettyFlow;
        int indent;
        int indicatorIndent;
        int lineWidth;
        boolean includeNulls;
        Set<String> excludedFields;
        
        private StyleConfig() {
            // Use builder
        }
        
        /**
         * Default configuration: Option C from discussion
         * - Categories and codes in BLOCK
         * - PeriodDto lists in FLOW
         * - Nulls included
         */
        public static final StyleConfig DEFAULT = builder()
            .defaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
            .prettyFlow(true)
            .indent(2)
            .indicatorIndent(0)
            .lineWidth(120)
            .includeNulls(true)
            .build();
        
        /**
         * Everything in FLOW style (most compact)
         */
        public static final StyleConfig ALL_FLOW = builder()
            .defaultFlowStyle(DumperOptions.FlowStyle.FLOW)
            .prettyFlow(true)
            .includeNulls(true)
            .build();
        
        /**
         * Everything in BLOCK style (most readable)
         */
        public static final StyleConfig ALL_BLOCK = builder()
            .defaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
            .prettyFlow(false)
            .includeNulls(true)
            .build();
        
        public static Builder builder() {
            return new Builder();
        }
        
        public static class Builder {
            private final StyleConfig config = new StyleConfig();
            
            public Builder defaultFlowStyle(DumperOptions.FlowStyle style) {
                config.defaultFlowStyle = style;
                return this;
            }
            
            public Builder prettyFlow(boolean pretty) {
                config.prettyFlow = pretty;
                return this;
            }
            
            public Builder indent(int indent) {
                config.indent = indent;
                return this;
            }
            
            public Builder indicatorIndent(int indent) {
                config.indicatorIndent = indent;
                return this;
            }
            
            public Builder lineWidth(int width) {
                config.lineWidth = width;
                return this;
            }
            
            public Builder includeNulls(boolean include) {
                config.includeNulls = include;
                return this;
            }
            
            public Builder excludeFields(Set<String> fields) {
                config.excludedFields = fields;
                return this;
            }
            
            public StyleConfig build() {
                // Set defaults
                if (config.indent == 0) config.indent = 2;
                if (config.lineWidth == 0) config.lineWidth = 120;
                if (config.defaultFlowStyle == null) {
                    config.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK;
                }
                return config;
            }
        }
    }
    
    /**
     * Custom PropertyUtils for excluding specific fields.
     */
    private static class CustomPropertyUtils extends org.yaml.snakeyaml.introspector.PropertyUtils {
        private final Set<String> excludedFields;
        
        public CustomPropertyUtils(Set<String> excludedFields) {
            this.excludedFields = excludedFields;
        }
        
        @Override
        protected Set<Property> createPropertySet(Class<?> type, org.yaml.snakeyaml.introspector.BeanAccess bAccess) {
            Set<Property> properties = super.createPropertySet(type, bAccess);
            
            // Filter out excluded fields
            if (excludedFields != null && !excludedFields.isEmpty()) {
                properties.removeIf(prop -> excludedFields.contains(prop.getName()));
            }
            
            return properties;
        }
    }
}
