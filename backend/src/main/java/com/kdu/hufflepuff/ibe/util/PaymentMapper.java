package com.kdu.hufflepuff.ibe.util;

import com.kdu.hufflepuff.ibe.model.dto.in.PaymentDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentMapper {

    public static PaymentDTO fromFormData(Map<String, String> formData) {
        return PaymentDTO.builder()
            .cardName(formData.get("cardName"))
            .cvv(formData.get("cvv"))
            .expMonth(formData.get("expMonth"))
            .expYear(formData.get("expYear"))
            .build();
    }

    public static PaymentDTO fromFormDataWithValidation(Map<String, String> formData) {
        validateRequiredFields(formData);
        validateCardDetails(formData);
        return fromFormData(formData);
    }

    private static void validateRequiredFields(Map<String, String> formData) {
        validateField(formData, "cardName", "Card name is required");
        validateField(formData, "cvv", "CVV is required");
        validateField(formData, "expMonth", "Expiration month is required");
        validateField(formData, "expYear", "Expiration year is required");
    }

    private static void validateCardDetails(Map<String, String> formData) {
        // Validate CVV
        String cvv = formData.get("cvv");
        if (cvv != null && !cvv.matches("^[0-9]{3,4}$")) {
            throw new IllegalArgumentException("CVV must be 3 or 4 digits");
        }

        // Validate expiration month
        String expMonth = formData.get("expMonth");
        if (expMonth != null && !expMonth.matches("^(0[1-9]|1[0-2])$")) {
            throw new IllegalArgumentException("Expiration month must be between 01 and 12");
        }

        // Validate expiration year
        String expYear = formData.get("expYear");
        if (expYear != null && !expYear.matches("^[0-9]{2}$")) {
            throw new IllegalArgumentException("Expiration year must be 2 digits");
        }
    }

    private static void validateField(Map<String, String> formData, String fieldName, String errorMessage) {
        String value = formData.get(fieldName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
} 