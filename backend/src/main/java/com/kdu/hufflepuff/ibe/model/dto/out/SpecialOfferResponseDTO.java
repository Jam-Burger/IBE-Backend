package com.kdu.hufflepuff.ibe.model.dto.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialOfferResponseDTO {
    @NotNull
    @JsonProperty("property_id")
    private Long propertyId;

    @NotNull
    private Long id;

    @NotNull
    @JsonProperty("start_date")
    private LocalDate startDate;

    @NotNull
    @JsonProperty("end_date")
    private LocalDate endDate;

    @NotNull
    @Positive
    @JsonProperty("discount_percentage")
    private Double discountPercentage;

    @NotNull
    private String title;

    private String description;
} 