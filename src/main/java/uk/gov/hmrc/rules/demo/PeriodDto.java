package uk.gov.hmrc.cars.refdata.domain;

import java.time.LocalDateTime;

/**
 * Represents a period with start/end dates and description.
 * Matches the structure from your YAML reference data.
 */
public record PeriodDto(
    LocalDateTime startDate,
    LocalDateTime endDate,
    String description
) {
}
