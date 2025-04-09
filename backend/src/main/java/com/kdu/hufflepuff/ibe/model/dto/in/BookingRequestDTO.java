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
    @NotNull(message = "Tenant ID is required")
    private Map<String, String> formData;

    @NotNull(message = "Date range is required")
    private DateRange dateRange;

    @NotNull(message = "Room count is required")
    private Integer roomCount;

    @NotNull(message = "Guest details are required")
    private Map<String, Integer> guests;

    @NotNull(message = "Bed count is required")
    private Integer bedCount;

    @NotNull(message = "Room type id is required")
    private Long roomTypeId;

    private String promotionId;

    @NotNull(message = "Total amount is required")
    private Double totalAmount;

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