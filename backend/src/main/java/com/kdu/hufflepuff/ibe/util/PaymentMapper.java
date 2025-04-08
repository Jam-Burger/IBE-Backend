package com.kdu.hufflepuff.ibe.util;

import com.kdu.hufflepuff.ibe.model.dto.in.PaymentDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentMapper {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static PaymentDTO fromFormData(Map<String, String> formData) {
        return PaymentDTO.builder()
            .cardNumber(formData.get("cardNumber"))
            .cvv(formData.get("cvv"))
            .expMonth(formData.get("expMonth"))
            .expYear(formData.get("expYear"))
            .build();
    }

    public static PaymentDTO fromFormDataWithValidation(Map<String, String> formData) {
        PaymentDTO paymentDTO = fromFormData(formData);
        validatePaymentDTO(paymentDTO);
        return paymentDTO;
    }

    private static void validatePaymentDTO(PaymentDTO paymentDTO) {
        Set<ConstraintViolation<PaymentDTO>> violations = validator.validate(paymentDTO);
        if (!violations.isEmpty()) {
            String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(errorMessage);
        }
    }
} 