package com.kdu.hufflepuff.ibe.model.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^\\d{16}$", message = "Card number must be exactly 16 digits")
    private String cardNumber;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^\\d{3,4}$", message = "CVV must be 3 or 4 digits")
    private String cvv;

    @Range(min = 1, max = 12, message = "Expiration month must be between 1 and 12")
    private Integer expMonth;

    @Range(min = 2000, max = 2100, message = "Expiration year must be between 2000 and 2100")
    private Integer expYear;
} 