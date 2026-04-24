package uk.gov.hmrc.cars.importer.writer;

public interface ReferenceDataWriter<T> {
    void write(T data, String destination) throws Exception;
}
