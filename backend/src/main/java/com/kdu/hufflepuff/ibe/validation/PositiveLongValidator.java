package com.kdu.hufflepuff.ibe.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for the PositiveLong annotation.
 * Ensures that a Long value is positive (greater than zero).
 */
public class PositiveLongValidator implements ConstraintValidator<PositiveLong, Long> {
    @Override
    public void initialize(PositiveLong constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value > 0;
    }
} 