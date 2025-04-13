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
import java.util.ArrayList;
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
        Banner banner = createValidBanner();
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
        assertThat(getBanner.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("LandingBanner");

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

    @Test
    void testLengthOfStayValidation() {
        // Given
        LandingPageConfigModel.LengthOfStay lengthOfStay = new LandingPageConfigModel.LengthOfStay();
        lengthOfStay.setMin(5);
        lengthOfStay.setMax(3); // Invalid: max < min

        // When
        Set<ConstraintViolation<LandingPageConfigModel.LengthOfStay>> violations = validator.validate(lengthOfStay);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Maximum length of stay must be greater than or equal to minimum length of stay");
    }

    @Test
    void testLengthOfStayGettersAndSetters() {
        // Given
        LandingPageConfigModel.LengthOfStay lengthOfStay = new LandingPageConfigModel.LengthOfStay();
        int min = 3;
        int max = 14;

        // When
        lengthOfStay.setMin(min);
        lengthOfStay.setMax(max);

        // Then
        assertThat(lengthOfStay.getMin()).isEqualTo(min);
        assertThat(lengthOfStay.getMax()).isEqualTo(max);
    }

    @Test
    void testLengthOfStayDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<LandingPageConfigModel.LengthOfStay> clazz = LandingPageConfigModel.LengthOfStay.class;

        // Then
        Method getMin = clazz.getMethod("getMin");
        assertThat(getMin.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Min");

        Method getMax = clazz.getMethod("getMax");
        assertThat(getMax.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Max");
    }

    @Test
    void testLengthOfStayMinValidation() {
        // Given
        LandingPageConfigModel.LengthOfStay lengthOfStay = new LandingPageConfigModel.LengthOfStay();
        lengthOfStay.setMin(0); // Invalid: min must be at least 1
        lengthOfStay.setMax(10);

        // When
        Set<ConstraintViolation<LandingPageConfigModel.LengthOfStay>> violations = validator.validate(lengthOfStay);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Minimum length of stay must be at least 1");
    }

    @Test
    void testLengthOfStayMaxValidation() {
        // Given
        LandingPageConfigModel.LengthOfStay lengthOfStay = new LandingPageConfigModel.LengthOfStay();
        lengthOfStay.setMin(1);
        lengthOfStay.setMax(0); // Invalid: max must be at least 1 and max >= min

        // When
        Set<ConstraintViolation<LandingPageConfigModel.LengthOfStay>> violations = validator.validate(lengthOfStay);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Maximum length of stay must be at least 1",
                "Maximum length of stay must be greater than or equal to minimum length of stay"
            );
    }

    @Test
    void testGuestOptionsValidation() {
        // Given
        LandingPageConfigModel.GuestOptions guestOptions = new LandingPageConfigModel.GuestOptions();
        guestOptions.setEnabled(true);
        guestOptions.setMin(5);
        guestOptions.setMax(3); // Invalid: max < min
        guestOptions.setCategories(List.of(createValidGuestCategory()));

        // When
        Set<ConstraintViolation<LandingPageConfigModel.GuestOptions>> violations = validator.validate(guestOptions);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Maximum guest count must be greater than or equal to minimum guest count");
    }

    @Test
    void testGuestOptionsGettersAndSetters() {
        // Given
        LandingPageConfigModel.GuestOptions guestOptions = new LandingPageConfigModel.GuestOptions();
        boolean enabled = true;
        int min = 1;
        int max = 4;
        List<LandingPageConfigModel.GuestCategory> categories = List.of(createValidGuestCategory());

        // When
        guestOptions.setEnabled(enabled);
        guestOptions.setMin(min);
        guestOptions.setMax(max);
        guestOptions.setCategories(categories);

        // Then
        assertThat(guestOptions.isEnabled()).isEqualTo(enabled);
        assertThat(guestOptions.getMin()).isEqualTo(min);
        assertThat(guestOptions.getMax()).isEqualTo(max);
        assertThat(guestOptions.getCategories()).isEqualTo(categories);
    }

    @Test
    void testGuestOptionsDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<LandingPageConfigModel.GuestOptions> clazz = LandingPageConfigModel.GuestOptions.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getMin = clazz.getMethod("getMin");
        assertThat(getMin.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Min");

        Method getMax = clazz.getMethod("getMax");
        assertThat(getMax.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Max");

        Method getCategories = clazz.getMethod("getCategories");
        assertThat(getCategories.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Categories");
    }

    @Test
    void testGuestOptionsCategoriesValidation() {
        // Given
        LandingPageConfigModel.GuestOptions guestOptions = new LandingPageConfigModel.GuestOptions();
        guestOptions.setEnabled(true);
        guestOptions.setMin(1);
        guestOptions.setMax(4);
        guestOptions.setCategories(new ArrayList<>()); // Empty list is invalidEmail

        // When
        Set<ConstraintViolation<LandingPageConfigModel.GuestOptions>> violations = validator.validate(guestOptions);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("At least one guest category must be configured");
    }

    @Test
    void testGuestCategoryValidation() {
        // Given
        LandingPageConfigModel.GuestCategory category = new LandingPageConfigModel.GuestCategory();
        category.setName("Adult");
        category.setEnabled(true);
        category.setMin(0);
        category.setMax(2);
        category.setDefaultValue(3); // Invalid: default > max
        category.setLabel("Adults");

        // When
        Set<ConstraintViolation<LandingPageConfigModel.GuestCategory>> violations = validator.validate(category);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Default guest count must be between minimum and maximum values");
    }

    @Test
    void testGuestCategoryGettersAndSetters() {
        // Given
        LandingPageConfigModel.GuestCategory category = new LandingPageConfigModel.GuestCategory();
        String name = "Adult";
        boolean enabled = true;
        int min = 0;
        int max = 2;
        int defaultValue = 1;
        String label = "Adults";

        // When
        category.setName(name);
        category.setEnabled(enabled);
        category.setMin(min);
        category.setMax(max);
        category.setDefaultValue(defaultValue);
        category.setLabel(label);

        // Then
        assertThat(category.getName()).isEqualTo(name);
        assertThat(category.isEnabled()).isEqualTo(enabled);
        assertThat(category.getMin()).isEqualTo(min);
        assertThat(category.getMax()).isEqualTo(max);
        assertThat(category.getDefaultValue()).isEqualTo(defaultValue);
        assertThat(category.getLabel()).isEqualTo(label);
    }

    @Test
    void testGuestCategoryDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<LandingPageConfigModel.GuestCategory> clazz = LandingPageConfigModel.GuestCategory.class;

        // Then
        Method getName = clazz.getMethod("getName");
        assertThat(getName.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Name");

        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getMin = clazz.getMethod("getMin");
        assertThat(getMin.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Min");

        Method getMax = clazz.getMethod("getMax");
        assertThat(getMax.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Max");

        Method getDefaultValue = clazz.getMethod("getDefaultValue");
        assertThat(getDefaultValue.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Default");

        Method getLabel = clazz.getMethod("getLabel");
        assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");
    }

    @Test
    void testGuestCategoryNameValidation() {
        // Given
        LandingPageConfigModel.GuestCategory category = new LandingPageConfigModel.GuestCategory();
        category.setName(""); // Invalid: name is blank
        category.setEnabled(true);
        category.setMin(0);
        category.setMax(2);
        category.setDefaultValue(1);
        category.setLabel("Adults");

        // When
        Set<ConstraintViolation<LandingPageConfigModel.GuestCategory>> violations = validator.validate(category);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Category name is required");
    }

    @Test
    void testGuestCategoryLabelValidation() {
        // Given
        LandingPageConfigModel.GuestCategory category = new LandingPageConfigModel.GuestCategory();
        category.setName("Adult");
        category.setEnabled(true);
        category.setMin(0);
        category.setMax(2);
        category.setDefaultValue(1);
        category.setLabel(""); // Invalid: label is blank

        // When
        Set<ConstraintViolation<LandingPageConfigModel.GuestCategory>> violations = validator.validate(category);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Category label is required");
    }

    @Test
    void testRoomOptionsValidation() {
        // Given
        LandingPageConfigModel.RoomOptions roomOptions = new LandingPageConfigModel.RoomOptions();
        roomOptions.setEnabled(true);
        roomOptions.setMin(2);
        roomOptions.setMax(1); // Invalid: max < min
        roomOptions.setDefaultValue(2); // Also invalidEmail because default can't be between min and max when min > max

        // When
        Set<ConstraintViolation<LandingPageConfigModel.RoomOptions>> violations = validator.validate(roomOptions);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Maximum room count must be greater than or equal to minimum value",
                "Default room count must be between minimum and maximum values"
            );
    }

    @Test
    void testRoomOptionsDefaultValueValidation() {
        // Given
        LandingPageConfigModel.RoomOptions roomOptions = new LandingPageConfigModel.RoomOptions();
        roomOptions.setEnabled(true);
        roomOptions.setMin(1);
        roomOptions.setMax(5);
        roomOptions.setDefaultValue(6); // Invalid: default > max

        // When
        Set<ConstraintViolation<LandingPageConfigModel.RoomOptions>> violations = validator.validate(roomOptions);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Default room count must be between minimum and maximum values");
    }

    @Test
    void testRoomOptionsGettersAndSetters() {
        // Given
        LandingPageConfigModel.RoomOptions roomOptions = new LandingPageConfigModel.RoomOptions();
        boolean enabled = true;
        int min = 1;
        int max = 5;
        int defaultValue = 2;

        // When
        roomOptions.setEnabled(enabled);
        roomOptions.setMin(min);
        roomOptions.setMax(max);
        roomOptions.setDefaultValue(defaultValue);

        // Then
        assertThat(roomOptions.isEnabled()).isEqualTo(enabled);
        assertThat(roomOptions.getMin()).isEqualTo(min);
        assertThat(roomOptions.getMax()).isEqualTo(max);
        assertThat(roomOptions.getDefaultValue()).isEqualTo(defaultValue);
    }

    @Test
    void testRoomOptionsDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<LandingPageConfigModel.RoomOptions> clazz = LandingPageConfigModel.RoomOptions.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getMin = clazz.getMethod("getMin");
        assertThat(getMin.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Min");

        Method getMax = clazz.getMethod("getMax");
        assertThat(getMax.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Max");

        Method getDefaultValue = clazz.getMethod("getDefaultValue");
        assertThat(getDefaultValue.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Default");
    }

    @Test
    void testAccessibilityGettersAndSetters() {
        // Given
        LandingPageConfigModel.Accessibility accessibility = new LandingPageConfigModel.Accessibility();
        boolean enabled = true;
        String label = "Accessibility Options";

        // When
        accessibility.setEnabled(enabled);
        accessibility.setLabel(label);

        // Then
        assertThat(accessibility.isEnabled()).isEqualTo(enabled);
        assertThat(accessibility.getLabel()).isEqualTo(label);
    }

    @Test
    void testAccessibilityDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<LandingPageConfigModel.Accessibility> clazz = LandingPageConfigModel.Accessibility.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getLabel = clazz.getMethod("getLabel");
        assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");
    }

    private LandingPageConfigModel createValidLandingPageConfig() {
        LandingPageConfigModel model = new LandingPageConfigModel();
        model.setBanner(createValidBanner());
        model.setSearchForm(createValidSearchForm());
        return model;
    }

    private Banner createValidBanner() {
        Banner banner = new Banner();
        banner.setEnabled(true);
        banner.setImageUrl("https://example.com/banner.jpg");
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
        category.setName("Adult");
        category.setEnabled(true);
        category.setMin(0);
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