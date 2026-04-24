package uk.gov.hmrc.cars.importer.writer;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class YamlReferenceDataWriter implements ReferenceDataWriter<Map<String, Object>> {

    @Override
    public void write(Map<String, Object> data, String destination) throws Exception {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        options.setPrettyFlow(true);
        options.setIndent(2);

        Representer representer = new Representer(options) {
            {
                this.representers.put(LocalDateTime.class, d ->
                    representScalar(Tag.STR, ((LocalDateTime) d).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                this.nullRepresenter = n -> representScalar(Tag.NULL, "null");
            }
        };

        Yaml yaml = new Yaml(representer, options);
        yaml.setBeanAccess(BeanAccess.FIELD); // Essential for PeriodDto Records

        try (FileWriter writer = new FileWriter(destination)) {
            yaml.dump(data, writer);
        }
    }
}
