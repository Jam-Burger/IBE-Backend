package com.kdu.hufflepuff.ibe.model.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    @NotBlank(message = "Card name is required")
    private String cardName;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^\\d{3,4}$", message = "CVV must be 3 or 4 digits")
    private String cvv;

    @NotBlank(message = "Expiration month is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Expiration month must be between 01 and 12")
    private String expMonth;

    @NotBlank(message = "Expiration year is required")
    @Pattern(regexp = "^\\d{2}$", message = "Expiration year must be 2 digits")
    private String expYear;
} 