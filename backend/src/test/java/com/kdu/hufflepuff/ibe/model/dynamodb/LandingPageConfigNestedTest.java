package com.kdu.hufflepuff.ibe.model.dynamodb;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LandingPageConfigNestedTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private LandingPageConfigModel.GuestCategory createValidGuestCategory() {
        LandingPageConfigModel.GuestCategory category = new LandingPageConfigModel.GuestCategory();
        category.setName("adult");
        category.setEnabled(true);
        category.setMin(1);
        category.setMax(2);
        category.setDefaultValue(1);
        category.setLabel("Adults");
        return category;
    }

    @Nested
    class LengthOfStayTest {
        @Test
        void testValidation_ValidModel() {
            // Given
            LandingPageConfigModel.LengthOfStay lengthOfStay = new LandingPageConfigModel.LengthOfStay();
            lengthOfStay.setMin(1);
            lengthOfStay.setMax(30);

            // When
            Set<ConstraintViolation<LandingPageConfigModel.LengthOfStay>> violations = validator.validate(lengthOfStay);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        void testValidation_InvalidRange() {
            // Given
            LandingPageConfigModel.LengthOfStay lengthOfStay = new LandingPageConfigModel.LengthOfStay();
            lengthOfStay.setMin(30);
            lengthOfStay.setMax(1);

            // When
            Set<ConstraintViolation<LandingPageConfigModel.LengthOfStay>> violations = validator.validate(lengthOfStay);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Maximum length of stay must be greater than or equal to minimum length of stay");
        }

        @Test
        void testDynamoDbAnnotations() throws NoSuchMethodException {
            // Given
            Class<LandingPageConfigModel.LengthOfStay> clazz = LandingPageConfigModel.LengthOfStay.class;

            // Then
            Method getMin = clazz.getMethod("getMin");
            assertThat(getMin.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Min");

            Method getMax = clazz.getMethod("getMax");
            assertThat(getMax.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Max");
        }
    }

    @Nested
    class GuestOptionsTest {
        @Test
        void testValidation_ValidModel() {
            // Given
            LandingPageConfigModel.GuestOptions options = new LandingPageConfigModel.GuestOptions();
            options.setEnabled(true);
            options.setMin(1);
            options.setMax(4);
            options.setCategories(List.of(createValidGuestCategory()));

            // When
            Set<ConstraintViolation<LandingPageConfigModel.GuestOptions>> violations = validator.validate(options);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        void testValidation_InvalidRange() {
            // Given
            LandingPageConfigModel.GuestOptions options = new LandingPageConfigModel.GuestOptions();
            options.setEnabled(true);
            options.setMin(4);
            options.setMax(1);
            options.setCategories(List.of(createValidGuestCategory()));

            // When
            Set<ConstraintViolation<LandingPageConfigModel.GuestOptions>> violations = validator.validate(options);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Maximum guest count must be greater than or equal to minimum guest count");
        }

        @Test
        void testDynamoDbAnnotations() throws NoSuchMethodException {
            // Given
            Class<LandingPageConfigModel.GuestOptions> clazz = LandingPageConfigModel.GuestOptions.class;

            // Then
            Method getEnabled = clazz.getMethod("isEnabled");
            assertThat(getEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

            Method getMin = clazz.getMethod("getMin");
            assertThat(getMin.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Min");

            Method getMax = clazz.getMethod("getMax");
            assertThat(getMax.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Max");

            Method getCategories = clazz.getMethod("getCategories");
            assertThat(getCategories.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Categories");
        }
    }

    @Nested
    class GuestCategoryTest {
        @Test
        void testValidation_ValidModel() {
            // Given
            LandingPageConfigModel.GuestCategory category = createValidGuestCategory();

            // When
            Set<ConstraintViolation<LandingPageConfigModel.GuestCategory>> violations = validator.validate(category);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        void testValidation_InvalidDefaultValue() {
            // Given
            LandingPageConfigModel.GuestCategory category = createValidGuestCategory();
            category.setDefaultValue(10); // Outside min-max range

            // When
            Set<ConstraintViolation<LandingPageConfigModel.GuestCategory>> violations = validator.validate(category);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Default guest count must be between minimum and maximum values");
        }

        @Test
        void testDynamoDbAnnotations() throws NoSuchMethodException {
            // Given
            Class<LandingPageConfigModel.GuestCategory> clazz = LandingPageConfigModel.GuestCategory.class;

            // Then
            Method getName = clazz.getMethod("getName");
            assertThat(getName.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Name");

            Method getEnabled = clazz.getMethod("isEnabled");
            assertThat(getEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

            Method getMin = clazz.getMethod("getMin");
            assertThat(getMin.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Min");

            Method getMax = clazz.getMethod("getMax");
            assertThat(getMax.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Max");

            Method getDefaultValue = clazz.getMethod("getDefaultValue");
            assertThat(getDefaultValue.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Default");

            Method getLabel = clazz.getMethod("getLabel");
            assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");
        }
    }

    @Nested
    class RoomOptionsTest {
        @Test
        void testValidation_ValidModel() {
            // Given
            LandingPageConfigModel.RoomOptions options = new LandingPageConfigModel.RoomOptions();
            options.setEnabled(true);
            options.setMin(1);
            options.setMax(5);
            options.setDefaultValue(2);

            // When
            Set<ConstraintViolation<LandingPageConfigModel.RoomOptions>> violations = validator.validate(options);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        void testValidation_InvalidDefaultValue() {
            // Given
            LandingPageConfigModel.RoomOptions options = new LandingPageConfigModel.RoomOptions();
            options.setEnabled(true);
            options.setMin(1);
            options.setMax(5);
            options.setDefaultValue(10); // Outside min-max range

            // When
            Set<ConstraintViolation<LandingPageConfigModel.RoomOptions>> violations = validator.validate(options);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Default room count must be between minimum and maximum values");
        }

        @Test
        void testDynamoDbAnnotations() throws NoSuchMethodException {
            // Given
            Class<LandingPageConfigModel.RoomOptions> clazz = LandingPageConfigModel.RoomOptions.class;

            // Then
            Method getEnabled = clazz.getMethod("isEnabled");
            assertThat(getEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

            Method getMin = clazz.getMethod("getMin");
            assertThat(getMin.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Min");

            Method getMax = clazz.getMethod("getMax");
            assertThat(getMax.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Max");

            Method getDefaultValue = clazz.getMethod("getDefaultValue");
            assertThat(getDefaultValue.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Default");
        }
    }

    @Nested
    class AccessibilityTest {
        @Test
        void testValidation_ValidModel() {
            // Given
            LandingPageConfigModel.Accessibility accessibility = new LandingPageConfigModel.Accessibility();
            accessibility.setEnabled(true);
            accessibility.setLabel("Accessibility Options");

            // When
            Set<ConstraintViolation<LandingPageConfigModel.Accessibility>> violations = validator.validate(accessibility);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        void testValidation_InvalidLabel() {
            // Given
            LandingPageConfigModel.Accessibility accessibility = new LandingPageConfigModel.Accessibility();
            accessibility.setEnabled(true);
            accessibility.setLabel("a".repeat(256)); // Too long label

            // When
            Set<ConstraintViolation<LandingPageConfigModel.Accessibility>> violations = validator.validate(accessibility);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                .isEqualTo("Accessibility label cannot exceed 255 characters");
        }

        @Test
        void testDynamoDbAnnotations() throws NoSuchMethodException {
            // Given
            Class<LandingPageConfigModel.Accessibility> clazz = LandingPageConfigModel.Accessibility.class;

            // Then
            Method getEnabled = clazz.getMethod("isEnabled");
            assertThat(getEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

            Method getLabel = clazz.getMethod("getLabel");
            assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");
        }
    }
} 