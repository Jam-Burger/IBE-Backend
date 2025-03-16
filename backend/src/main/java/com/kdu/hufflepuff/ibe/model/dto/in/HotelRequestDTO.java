package com.kdu.hufflepuff.ibe.model.dto.in;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelRequestDTO {

    @NotBlank(message = "Hotel name cannot be empty")
    @Size(max = 100, message = "Hotel name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Hotel address cannot be empty")
    @Size(max = 200, message = "Hotel address cannot exceed 200 characters")
    private String address;

    @Min(value = 1, message = "Star rating must be at least 1")
    @Max(value = 5, message = "Star rating cannot exceed 5")
    private int starRating;

    @NotNull(message = "Price per night is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal pricePerNight;

    @NotBlank(message = "Hotel description cannot be empty")
    private String description;
}

