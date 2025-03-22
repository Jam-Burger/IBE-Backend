package com.kdu.hufflepuff.ibe.model.dto.in;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class CreateSpecialDiscountRequest {
    @NotNull(message = "Property ID is required")
    private Long propertyId;

    @NotNull(message = "Discount date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate discountDate;

    @NotNull(message = "Discount percentage is required")
    @Min(value = 0, message = "Discount percentage must be between 0 and 100")
    @Max(value = 100, message = "Discount percentage must be between 0 and 100")
    private Double discountPercentage;
} 