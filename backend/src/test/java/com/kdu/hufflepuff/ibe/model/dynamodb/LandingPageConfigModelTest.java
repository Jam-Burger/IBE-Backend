package com.kdu.hufflepuff.ibe.model.dynamodb;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbFlatten;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LandingPageConfigModelTest {

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
        LandingPageConfigModel model = new LandingPageConfigModel();
        String pageTitle = "Welcome to Our Hotel";
        LandingPageConfigModel.Banner banner = createValidBanner();
        LandingPageConfigModel.SearchForm searchForm = createValidSearchForm();

        // When
        model.setBanner(banner);
        model.setSearchForm(searchForm);

        // Then
        assertThat(model.getBanner()).isEqualTo(banner);
        assertThat(model.getSearchForm()).isEqualTo(searchForm);
    }

    @Test
    void testDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<LandingPageConfigModel> clazz = LandingPageConfigModel.class;

        // Then
        Method getBanner = clazz.getMethod("getBanner");
        assertThat(getBanner.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Banner");

        Method getSearchForm = clazz.getMethod("getSearchForm");
        assertThat(getSearchForm.isAnnotationPresent(DynamoDbFlatten.class)).isTrue();
        assertThat(getSearchForm.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("SearchForm");
    }

    @Test
    void testValidation_ValidModel() {
        // Given
        LandingPageConfigModel model = createValidLandingPageConfig();

        // When
        Set<ConstraintViolation<LandingPageConfigModel>> violations = validator.validate(model);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_InvalidModel() {
        // Given
        LandingPageConfigModel model = new LandingPageConfigModel();

        // When
        Set<ConstraintViolation<LandingPageConfigModel>> violations = validator.validate(model);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Banner configuration is required",
                "Search form configuration is required"
            );
    }

    @Test
    void testBannerValidation() {
        // Given
        LandingPageConfigModel.Banner banner = new LandingPageConfigModel.Banner();
        banner.setEnabled(true);
        banner.setImageUrl("https://example.com/image.jpg" + "a".repeat(1000)); // Too long URL

        // When
        Set<ConstraintViolation<LandingPageConfigModel.Banner>> violations = validator.validate(banner);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Image URL cannot exceed 1024 characters");
    }

    @Test
    void testSearchFormValidation() {
        // Given
        LandingPageConfigModel.SearchForm searchForm = new LandingPageConfigModel.SearchForm();

        // When
        Set<ConstraintViolation<LandingPageConfigModel.SearchForm>> violations = validator.validate(searchForm);

        // Then
        assertThat(violations).hasSize(4);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Length of stay configuration is required",
                "Guest options configuration is required",
                "Room options configuration is required",
                "Accessibility configuration is required"
            );
    }

    private LandingPageConfigModel createValidLandingPageConfig() {
        LandingPageConfigModel model = new LandingPageConfigModel();
        model.setBanner(createValidBanner());
        model.setSearchForm(createValidSearchForm());
        return model;
    }

    private LandingPageConfigModel.Banner createValidBanner() {
        LandingPageConfigModel.Banner banner = new LandingPageConfigModel.Banner();
        banner.setEnabled(true);
        banner.setImageUrl("https://example.com/image.jpg");
        return banner;
    }

    private LandingPageConfigModel.SearchForm createValidSearchForm() {
        LandingPageConfigModel.SearchForm searchForm = new LandingPageConfigModel.SearchForm();
        searchForm.setLengthOfStay(createValidLengthOfStay());
        searchForm.setGuestOptions(createValidGuestOptions());
        searchForm.setRoomOptions(createValidRoomOptions());
        searchForm.setAccessibility(createValidAccessibility());
        return searchForm;
    }

    private LandingPageConfigModel.LengthOfStay createValidLengthOfStay() {
        LandingPageConfigModel.LengthOfStay lengthOfStay = new LandingPageConfigModel.LengthOfStay();
        lengthOfStay.setMin(1);
        lengthOfStay.setMax(30);
        return lengthOfStay;
    }

    private LandingPageConfigModel.GuestOptions createValidGuestOptions() {
        LandingPageConfigModel.GuestOptions guestOptions = new LandingPageConfigModel.GuestOptions();
        guestOptions.setEnabled(true);
        guestOptions.setMin(1);
        guestOptions.setMax(4);
        guestOptions.setCategories(List.of(createValidGuestCategory()));
        return guestOptions;
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

    private LandingPageConfigModel.RoomOptions createValidRoomOptions() {
        LandingPageConfigModel.RoomOptions roomOptions = new LandingPageConfigModel.RoomOptions();
        roomOptions.setEnabled(true);
        roomOptions.setMin(1);
        roomOptions.setMax(5);
        roomOptions.setDefaultValue(1);
        return roomOptions;
    }

    private LandingPageConfigModel.Accessibility createValidAccessibility() {
        LandingPageConfigModel.Accessibility accessibility = new LandingPageConfigModel.Accessibility();
        accessibility.setEnabled(true);
        accessibility.setLabel("Accessibility Options");
        return accessibility;
    }
} 