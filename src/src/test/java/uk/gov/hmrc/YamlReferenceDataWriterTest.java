@SpringBootTest
class YamlReferenceDataWriterTest {
    @Autowired private YamlReferenceDataWriter writer;

    @Test
    void testWriteFormat() throws Exception {
        Path path = Files.createTempFile("test-output", ".yaml");
        
        // Mock data matching your debugger view
        var dto = new PeriodDto(LocalDateTime.of(2001, 1, 1, 0, 0), null, "Desc");
        var data = Map.of("AdditionalInformationTypes", Map.of("values", Map.of("ACA", List.of(dto))));

        writer.write(data, path.toString());

        String content = Files.readString(path);
        assertTrue(content.contains("\"endDate\": null"));
        assertTrue(content.contains("\"ACA\": ["));
    }
}
