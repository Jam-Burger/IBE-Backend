package com.kdu.hufflepuff.ibe.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom validation annotation to ensure a Long value is positive.
 * Can be applied to Long parameters or fields.
 */
@Documented
@Constraint(validatedBy = PositiveLongValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveLong {
    String message() default "Value must be a positive number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 