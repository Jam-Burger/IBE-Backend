package com.kdu.hufflepuff.ibe.model.dto.in;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSpecialDiscountRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testGettersAndSetters() {
        // Given
        CreateSpecialDiscountRequest request = new CreateSpecialDiscountRequest();
        LocalDate discountDate = LocalDate.now();
        Double discountPercentage = 15.0;

        // When
        request.setDiscountDate(discountDate);
        request.setDiscountPercentage(discountPercentage);

        // Then
        assertThat(request.getDiscountDate()).isEqualTo(discountDate);
        assertThat(request.getDiscountPercentage()).isEqualTo(discountPercentage);
    }

    @Test
    void testValidation_ValidRequest() {
        // Given
        CreateSpecialDiscountRequest request = new CreateSpecialDiscountRequest();
        request.setDiscountDate(LocalDate.now());
        request.setDiscountPercentage(15.0);

        // When
        Set<ConstraintViolation<CreateSpecialDiscountRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_InvalidRequest() {
        // Given
        CreateSpecialDiscountRequest request = new CreateSpecialDiscountRequest();
        request.setDiscountDate(null);
        request.setDiscountPercentage(null);

        // When
        Set<ConstraintViolation<CreateSpecialDiscountRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Discount date is required",
                "Discount percentage is required"
            );
    }

    @Test
    void testValidation_InvalidDiscountPercentage() {
        // Given
        CreateSpecialDiscountRequest request = new CreateSpecialDiscountRequest();
        request.setDiscountDate(LocalDate.now());
        request.setDiscountPercentage(101.0); // Invalid: > 100%

        // When
        Set<ConstraintViolation<CreateSpecialDiscountRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Discount percentage must be between 0 and 100");
    }
} 