package com.kdu.hufflepuff.ibe.model.dto.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    @NotNull
    private Map<String, String> formData;

    @NotNull
    private Long propertyId;

    @NotNull
    private DateRange dateRange;

    @NotNull
    private Integer roomCount;

    @NotNull
    private Map<String, Integer> guests;

    @NotNull
    private Integer bedCount;

    @NotNull
    private Long roomTypeId;

    private String promotionId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRange {
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate from;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate to;
    }
}