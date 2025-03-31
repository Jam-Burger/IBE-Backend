package com.kdu.hufflepuff.ibe.model.dynamodb;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

import java.lang.reflect.Method;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BannerTest {

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
        Banner banner = new Banner();
        boolean enabled = true;
        String imageUrl = "https://example.com/banner.jpg";

        // When
        banner.setEnabled(enabled);
        banner.setImageUrl(imageUrl);

        // Then
        assertThat(banner.isEnabled()).isEqualTo(enabled);
        assertThat(banner.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    void testDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<Banner> clazz = Banner.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getImageUrl = clazz.getMethod("getImageUrl");
        assertThat(getImageUrl.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("ImageUrl");
    }

    @Test
    void testValidation_ValidModel() {
        // Given
        Banner banner = createValidBanner();

        // When
        Set<ConstraintViolation<Banner>> violations = validator.validate(banner);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_InvalidModel() {
        // Given
        Banner banner = new Banner();
        banner.setEnabled(true);
        banner.setImageUrl("https://example.com/image.jpg" + "a".repeat(1000)); // Too long URL

        // When
        Set<ConstraintViolation<Banner>> violations = validator.validate(banner);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Image URL cannot exceed 1024 characters");
    }

    private Banner createValidBanner() {
        Banner banner = new Banner();
        banner.setEnabled(true);
        banner.setImageUrl("https://example.com/banner.jpg");
        return banner;
    }
} 