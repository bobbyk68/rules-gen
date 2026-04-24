package uk.gov.hmrc.cars.importer.writer;

import java.util.Map;

@Service
public class DatabaseReferenceDataWriter implements ReferenceDataWriter<Map<String, Object>> {
    @Override
    public void write(Map<String, Object> data, String destination) throws Exception {
        // Their JDBC/Hibernate logic to save the same Map to the DB
    }
}
