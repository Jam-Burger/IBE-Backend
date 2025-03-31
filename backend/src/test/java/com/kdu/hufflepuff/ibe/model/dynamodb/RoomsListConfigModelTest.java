package com.kdu.hufflepuff.ibe.model.dynamodb;

import com.kdu.hufflepuff.ibe.model.enums.SortOption;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RoomsListConfigModelTest {

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
        RoomsListConfigModel model = new RoomsListConfigModel();
        Banner banner = createValidBanner();
        RoomsListConfigModel.Steps steps = createValidSteps();
        RoomsListConfigModel.Filters filters = createValidFilters();

        // When
        model.setBanner(banner);
        model.setSteps(steps);
        model.setFilters(filters);

        // Then
        assertThat(model.getBanner()).isEqualTo(banner);
        assertThat(model.getSteps()).isEqualTo(steps);
        assertThat(model.getFilters()).isEqualTo(filters);
    }

    @Test
    void testDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel> clazz = RoomsListConfigModel.class;

        // Then
        Method getBanner = clazz.getMethod("getBanner");
        assertThat(getBanner.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("RoomsListBanner");

        Method getSteps = clazz.getMethod("getSteps");
        assertThat(getSteps.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Steps");

        Method getFilters = clazz.getMethod("getFilters");
        assertThat(getFilters.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Filters");
    }

    @Test
    void testValidation_ValidModel() {
        // Given
        RoomsListConfigModel model = createValidRoomsListConfig();

        // When
        Set<ConstraintViolation<RoomsListConfigModel>> violations = validator.validate(model);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_InvalidModel() {
        // Given
        RoomsListConfigModel model = new RoomsListConfigModel();

        // When
        Set<ConstraintViolation<RoomsListConfigModel>> violations = validator.validate(model);

        // Then
        assertThat(violations).hasSize(3);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Banner configuration is required",
                "Steps configuration is required",
                "Filters configuration is required"
            );
    }

    @Test
    void testStepsValidation() {
        // Given
        RoomsListConfigModel.Steps steps = new RoomsListConfigModel.Steps();
        steps.setEnabled(true);
        steps.setLabels(List.of()); // Empty list is invalid

        // When
        Set<ConstraintViolation<RoomsListConfigModel.Steps>> violations = validator.validate(steps);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("At least one step label must be configured");
    }

    @Test
    void testStepsGettersAndSetters() {
        // Given
        RoomsListConfigModel.Steps steps = new RoomsListConfigModel.Steps();
        boolean enabled = true;
        List<String> labels = List.of("Step 1", "Step 2", "Step 3");

        // When
        steps.setEnabled(enabled);
        steps.setLabels(labels);

        // Then
        assertThat(steps.isEnabled()).isEqualTo(enabled);
        assertThat(steps.getLabels()).isEqualTo(labels);
    }

    @Test
    void testStepsDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.Steps> clazz = RoomsListConfigModel.Steps.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getLabels = clazz.getMethod("getLabels");
        assertThat(getLabels.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Labels");
    }

    @Test
    void testFiltersValidation() {
        // Given
        RoomsListConfigModel.Filters filters = new RoomsListConfigModel.Filters();

        // When
        Set<ConstraintViolation<RoomsListConfigModel.Filters>> violations = validator.validate(filters);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Sort options configuration is required",
                "Filter groups configuration is required"
            );
    }

    @Test
    void testFiltersGettersAndSetters() {
        // Given
        RoomsListConfigModel.Filters filters = new RoomsListConfigModel.Filters();
        RoomsListConfigModel.SortOptions sortOptions = createValidSortOptions();
        RoomsListConfigModel.FilterGroups filterGroups = createValidFilterGroups();

        // When
        filters.setSortOptions(sortOptions);
        filters.setFilterGroups(filterGroups);

        // Then
        assertThat(filters.getSortOptions()).isEqualTo(sortOptions);
        assertThat(filters.getFilterGroups()).isEqualTo(filterGroups);
    }

    @Test
    void testFiltersDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.Filters> clazz = RoomsListConfigModel.Filters.class;

        // Then
        Method getSortOptions = clazz.getMethod("getSortOptions");
        assertThat(getSortOptions.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("SortOptions");

        Method getFilterGroups = clazz.getMethod("getFilterGroups");
        assertThat(getFilterGroups.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("FilterGroups");
    }

    @Test
    void testSortOptionsValidation() {
        // Given
        RoomsListConfigModel.SortOptions sortOptions = new RoomsListConfigModel.SortOptions();
        sortOptions.setEnabled(true);
        sortOptions.setDefaultSort(SortOption.PRICE_LOW_TO_HIGH);
        sortOptions.setOptions(List.of()); // Empty list is invalid

        // When
        Set<ConstraintViolation<RoomsListConfigModel.SortOptions>> violations = validator.validate(sortOptions);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("At least one sort option must be configured");
    }

    @Test
    void testSortOptionConfigValidation() {
        // Given
        RoomsListConfigModel.SortOptionConfig config = new RoomsListConfigModel.SortOptionConfig();
        config.setValue(SortOption.PRICE_LOW_TO_HIGH);
        config.setEnabled(true);
        // Label is missing

        // When
        Set<ConstraintViolation<RoomsListConfigModel.SortOptionConfig>> violations = validator.validate(config);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Sort option label is required");
    }

    @Test
    void testFilterGroupsValidation() {
        // Given
        RoomsListConfigModel.FilterGroups filterGroups = new RoomsListConfigModel.FilterGroups();

        // When
        Set<ConstraintViolation<RoomsListConfigModel.FilterGroups>> violations = validator.validate(filterGroups);

        // Then
        assertThat(violations).hasSize(5);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Ratings filter configuration is required",
                "Bed types filter configuration is required",
                "Bed count filter configuration is required",
                "Room size filter configuration is required",
                "Amenities filter configuration is required"
            );
    }

    @Test
    void testRatingsFilterValidation() {
        // Given
        RoomsListConfigModel.RatingsFilter ratings = new RoomsListConfigModel.RatingsFilter();
        ratings.setEnabled(true);
        ratings.setLabel("Ratings");
        ratings.setOptions(List.of()); // Empty list is invalid

        // When
        Set<ConstraintViolation<RoomsListConfigModel.RatingsFilter>> violations = validator.validate(ratings);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("At least one rating option must be configured");
    }

    @Test
    void testRatingOptionValidation() {
        // Given
        RoomsListConfigModel.RatingOption option = new RoomsListConfigModel.RatingOption();
        option.setValue(5);
        option.setEnabled(true);
        // Label is missing

        // When
        Set<ConstraintViolation<RoomsListConfigModel.RatingOption>> violations = validator.validate(option);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Rating label is required");
    }

    @Test
    void testBedCountFilterValidation() {
        // Given
        RoomsListConfigModel.BedCountFilter bedCount = new RoomsListConfigModel.BedCountFilter();
        bedCount.setEnabled(true);
        bedCount.setLabel("Bed Count");
        bedCount.setMin(2);
        bedCount.setMax(1); // Invalid: max < min
        bedCount.setDefaultValue(2); // Also invalid because default can't be between min and max when min > max

        // When
        Set<ConstraintViolation<RoomsListConfigModel.BedCountFilter>> violations = validator.validate(bedCount);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Maximum bed count must be greater than or equal to minimum value",
                "Default bed count must be between minimum and maximum values"
            );
    }

    @Test
    void testBedCountFilterDefaultValueValidation() {
        // Given
        RoomsListConfigModel.BedCountFilter bedCount = new RoomsListConfigModel.BedCountFilter();
        bedCount.setEnabled(true);
        bedCount.setLabel("Bed Count");
        bedCount.setMin(1);
        bedCount.setMax(4);
        bedCount.setDefaultValue(5); // Invalid: default > max

        // When
        Set<ConstraintViolation<RoomsListConfigModel.BedCountFilter>> violations = validator.validate(bedCount);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Default bed count must be between minimum and maximum values");
    }

    @Test
    void testRoomSizeFilterValidation() {
        // Given
        RoomsListConfigModel.RoomSizeFilter roomSize = new RoomsListConfigModel.RoomSizeFilter();
        roomSize.setEnabled(true);
        roomSize.setLabel("Room Size");
        roomSize.setMin(50);
        roomSize.setMax(30); // Invalid: max < min

        // When
        Set<ConstraintViolation<RoomsListConfigModel.RoomSizeFilter>> violations = validator.validate(roomSize);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
            .isEqualTo("Maximum room size must be greater than or equal to minimum value");
    }

    @Test
    void testSortOptionsGettersAndSetters() {
        // Given
        RoomsListConfigModel.SortOptions sortOptions = new RoomsListConfigModel.SortOptions();
        boolean enabled = true;
        SortOption defaultSort = SortOption.PRICE_LOW_TO_HIGH;
        List<RoomsListConfigModel.SortOptionConfig> options = List.of(createValidSortOptionConfig());

        // When
        sortOptions.setEnabled(enabled);
        sortOptions.setDefaultSort(defaultSort);
        sortOptions.setOptions(options);

        // Then
        assertThat(sortOptions.isEnabled()).isEqualTo(enabled);
        assertThat(sortOptions.getDefaultSort()).isEqualTo(defaultSort);
        assertThat(sortOptions.getOptions()).isEqualTo(options);
    }

    @Test
    void testSortOptionsDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.SortOptions> clazz = RoomsListConfigModel.SortOptions.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getDefaultSort = clazz.getMethod("getDefaultSort");
        assertThat(getDefaultSort.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Default");

        Method getOptions = clazz.getMethod("getOptions");
        assertThat(getOptions.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Options");
    }

    @Test
    void testSortOptionConfigGettersAndSetters() {
        // Given
        RoomsListConfigModel.SortOptionConfig config = new RoomsListConfigModel.SortOptionConfig();
        SortOption value = SortOption.PRICE_HIGH_TO_LOW;
        String label = "Price: High to Low";
        boolean enabled = true;

        // When
        config.setValue(value);
        config.setLabel(label);
        config.setEnabled(enabled);

        // Then
        assertThat(config.getValue()).isEqualTo(value);
        assertThat(config.getLabel()).isEqualTo(label);
        assertThat(config.isEnabled()).isEqualTo(enabled);
    }

    @Test
    void testSortOptionConfigDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.SortOptionConfig> clazz = RoomsListConfigModel.SortOptionConfig.class;

        // Then
        Method getValue = clazz.getMethod("getValue");
        assertThat(getValue.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Value");

        Method getLabel = clazz.getMethod("getLabel");
        assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");

        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");
    }

    @Test
    void testFilterGroupsGettersAndSetters() {
        // Given
        RoomsListConfigModel.FilterGroups filterGroups = new RoomsListConfigModel.FilterGroups();
        RoomsListConfigModel.RatingsFilter ratings = createValidRatingsFilter();
        RoomsListConfigModel.BedTypesFilter bedTypes = createValidBedTypesFilter();
        RoomsListConfigModel.BedCountFilter bedCount = createValidBedCountFilter();
        RoomsListConfigModel.RoomSizeFilter roomSize = createValidRoomSizeFilter();
        RoomsListConfigModel.AmenitiesFilter amenities = createValidAmenitiesFilter();

        // When
        filterGroups.setRatings(ratings);
        filterGroups.setBedTypes(bedTypes);
        filterGroups.setBedCount(bedCount);
        filterGroups.setRoomSize(roomSize);
        filterGroups.setAmenities(amenities);

        // Then
        assertThat(filterGroups.getRatings()).isEqualTo(ratings);
        assertThat(filterGroups.getBedTypes()).isEqualTo(bedTypes);
        assertThat(filterGroups.getBedCount()).isEqualTo(bedCount);
        assertThat(filterGroups.getRoomSize()).isEqualTo(roomSize);
        assertThat(filterGroups.getAmenities()).isEqualTo(amenities);
    }

    @Test
    void testFilterGroupsDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.FilterGroups> clazz = RoomsListConfigModel.FilterGroups.class;

        // Then
        Method getRatings = clazz.getMethod("getRatings");
        assertThat(getRatings.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Ratings");

        Method getBedTypes = clazz.getMethod("getBedTypes");
        assertThat(getBedTypes.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("BedTypes");

        Method getBedCount = clazz.getMethod("getBedCount");
        assertThat(getBedCount.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("BedCount");

        Method getRoomSize = clazz.getMethod("getRoomSize");
        assertThat(getRoomSize.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("RoomSize");

        Method getAmenities = clazz.getMethod("getAmenities");
        assertThat(getAmenities.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Amenities");
    }

    @Test
    void testRatingsFilterGettersAndSetters() {
        // Given
        RoomsListConfigModel.RatingsFilter ratings = new RoomsListConfigModel.RatingsFilter();
        boolean enabled = true;
        String label = "Ratings";
        List<RoomsListConfigModel.RatingOption> options = List.of(createValidRatingOption());

        // When
        ratings.setEnabled(enabled);
        ratings.setLabel(label);
        ratings.setOptions(options);

        // Then
        assertThat(ratings.isEnabled()).isEqualTo(enabled);
        assertThat(ratings.getLabel()).isEqualTo(label);
        assertThat(ratings.getOptions()).isEqualTo(options);
    }

    @Test
    void testRatingsFilterDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.RatingsFilter> clazz = RoomsListConfigModel.RatingsFilter.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getLabel = clazz.getMethod("getLabel");
        assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");

        Method getOptions = clazz.getMethod("getOptions");
        assertThat(getOptions.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Options");
    }

    @Test
    void testRatingOptionGettersAndSetters() {
        // Given
        RoomsListConfigModel.RatingOption option = new RoomsListConfigModel.RatingOption();
        Integer value = 5;
        String label = "5 Stars";
        boolean enabled = true;

        // When
        option.setValue(value);
        option.setLabel(label);
        option.setEnabled(enabled);

        // Then
        assertThat(option.getValue()).isEqualTo(value);
        assertThat(option.getLabel()).isEqualTo(label);
        assertThat(option.isEnabled()).isEqualTo(enabled);
    }

    @Test
    void testRatingOptionDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.RatingOption> clazz = RoomsListConfigModel.RatingOption.class;

        // Then
        Method getValue = clazz.getMethod("getValue");
        assertThat(getValue.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Value");

        Method getLabel = clazz.getMethod("getLabel");
        assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");

        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");
    }

    @Test
    void testBedTypesFilterGettersAndSetters() {
        // Given
        RoomsListConfigModel.BedTypesFilter bedTypes = new RoomsListConfigModel.BedTypesFilter();
        boolean enabled = true;
        String label = "Bed Types";

        // When
        bedTypes.setEnabled(enabled);
        bedTypes.setLabel(label);

        // Then
        assertThat(bedTypes.isEnabled()).isEqualTo(enabled);
        assertThat(bedTypes.getLabel()).isEqualTo(label);
    }

    @Test
    void testBedTypesFilterDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.BedTypesFilter> clazz = RoomsListConfigModel.BedTypesFilter.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getLabel = clazz.getMethod("getLabel");
        assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");
    }

    @Test
    void testBedCountFilterGettersAndSetters() {
        // Given
        RoomsListConfigModel.BedCountFilter bedCount = new RoomsListConfigModel.BedCountFilter();
        boolean enabled = true;
        String label = "Bed Count";
        int min = 1;
        int max = 4;
        int defaultValue = 2;

        // When
        bedCount.setEnabled(enabled);
        bedCount.setLabel(label);
        bedCount.setMin(min);
        bedCount.setMax(max);
        bedCount.setDefaultValue(defaultValue);

        // Then
        assertThat(bedCount.isEnabled()).isEqualTo(enabled);
        assertThat(bedCount.getLabel()).isEqualTo(label);
        assertThat(bedCount.getMin()).isEqualTo(min);
        assertThat(bedCount.getMax()).isEqualTo(max);
        assertThat(bedCount.getDefaultValue()).isEqualTo(defaultValue);
    }

    @Test
    void testBedCountFilterDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.BedCountFilter> clazz = RoomsListConfigModel.BedCountFilter.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getLabel = clazz.getMethod("getLabel");
        assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");

        Method getMin = clazz.getMethod("getMin");
        assertThat(getMin.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Min");

        Method getMax = clazz.getMethod("getMax");
        assertThat(getMax.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Max");

        Method getDefaultValue = clazz.getMethod("getDefaultValue");
        assertThat(getDefaultValue.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Default");
    }

    @Test
    void testRoomSizeFilterGettersAndSetters() {
        // Given
        RoomsListConfigModel.RoomSizeFilter roomSize = new RoomsListConfigModel.RoomSizeFilter();
        boolean enabled = true;
        String label = "Room Size";
        int min = 20;
        int max = 100;

        // When
        roomSize.setEnabled(enabled);
        roomSize.setLabel(label);
        roomSize.setMin(min);
        roomSize.setMax(max);

        // Then
        assertThat(roomSize.isEnabled()).isEqualTo(enabled);
        assertThat(roomSize.getLabel()).isEqualTo(label);
        assertThat(roomSize.getMin()).isEqualTo(min);
        assertThat(roomSize.getMax()).isEqualTo(max);
    }

    @Test
    void testRoomSizeFilterDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.RoomSizeFilter> clazz = RoomsListConfigModel.RoomSizeFilter.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getLabel = clazz.getMethod("getLabel");
        assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");

        Method getMin = clazz.getMethod("getMin");
        assertThat(getMin.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Min");

        Method getMax = clazz.getMethod("getMax");
        assertThat(getMax.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Max");
    }

    @Test
    void testAmenitiesFilterGettersAndSetters() {
        // Given
        RoomsListConfigModel.AmenitiesFilter amenities = new RoomsListConfigModel.AmenitiesFilter();
        boolean enabled = true;
        String label = "Amenities";

        // When
        amenities.setEnabled(enabled);
        amenities.setLabel(label);

        // Then
        assertThat(amenities.isEnabled()).isEqualTo(enabled);
        assertThat(amenities.getLabel()).isEqualTo(label);
    }

    @Test
    void testAmenitiesFilterDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<RoomsListConfigModel.AmenitiesFilter> clazz = RoomsListConfigModel.AmenitiesFilter.class;

        // Then
        Method isEnabled = clazz.getMethod("isEnabled");
        assertThat(isEnabled.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Enabled");

        Method getLabel = clazz.getMethod("getLabel");
        assertThat(getLabel.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Label");
    }

    private RoomsListConfigModel createValidRoomsListConfig() {
        RoomsListConfigModel model = new RoomsListConfigModel();
        model.setBanner(createValidBanner());
        model.setSteps(createValidSteps());
        model.setFilters(createValidFilters());
        return model;
    }

    private Banner createValidBanner() {
        Banner banner = new Banner();
        banner.setEnabled(true);
        banner.setImageUrl("https://example.com/banner.jpg");
        return banner;
    }

    private RoomsListConfigModel.Steps createValidSteps() {
        RoomsListConfigModel.Steps steps = new RoomsListConfigModel.Steps();
        steps.setEnabled(true);
        steps.setLabels(List.of("Step 1", "Step 2", "Step 3"));
        return steps;
    }

    private RoomsListConfigModel.Filters createValidFilters() {
        RoomsListConfigModel.Filters filters = new RoomsListConfigModel.Filters();
        filters.setSortOptions(createValidSortOptions());
        filters.setFilterGroups(createValidFilterGroups());
        return filters;
    }

    private RoomsListConfigModel.SortOptions createValidSortOptions() {
        RoomsListConfigModel.SortOptions sortOptions = new RoomsListConfigModel.SortOptions();
        sortOptions.setEnabled(true);
        sortOptions.setDefaultSort(SortOption.PRICE_LOW_TO_HIGH);
        sortOptions.setOptions(List.of(createValidSortOptionConfig()));
        return sortOptions;
    }

    private RoomsListConfigModel.SortOptionConfig createValidSortOptionConfig() {
        RoomsListConfigModel.SortOptionConfig config = new RoomsListConfigModel.SortOptionConfig();
        config.setValue(SortOption.PRICE_LOW_TO_HIGH);
        config.setLabel("Price: Low to High");
        config.setEnabled(true);
        return config;
    }

    private RoomsListConfigModel.FilterGroups createValidFilterGroups() {
        RoomsListConfigModel.FilterGroups filterGroups = new RoomsListConfigModel.FilterGroups();
        filterGroups.setRatings(createValidRatingsFilter());
        filterGroups.setBedTypes(createValidBedTypesFilter());
        filterGroups.setBedCount(createValidBedCountFilter());
        filterGroups.setRoomSize(createValidRoomSizeFilter());
        filterGroups.setAmenities(createValidAmenitiesFilter());
        return filterGroups;
    }

    private RoomsListConfigModel.RatingsFilter createValidRatingsFilter() {
        RoomsListConfigModel.RatingsFilter ratings = new RoomsListConfigModel.RatingsFilter();
        ratings.setEnabled(true);
        ratings.setLabel("Ratings");
        ratings.setOptions(List.of(createValidRatingOption()));
        return ratings;
    }

    private RoomsListConfigModel.RatingOption createValidRatingOption() {
        RoomsListConfigModel.RatingOption option = new RoomsListConfigModel.RatingOption();
        option.setValue(5);
        option.setLabel("5 Stars");
        option.setEnabled(true);
        return option;
    }

    private RoomsListConfigModel.BedTypesFilter createValidBedTypesFilter() {
        RoomsListConfigModel.BedTypesFilter bedTypes = new RoomsListConfigModel.BedTypesFilter();
        bedTypes.setEnabled(true);
        bedTypes.setLabel("Bed Types");
        return bedTypes;
    }

    private RoomsListConfigModel.BedCountFilter createValidBedCountFilter() {
        RoomsListConfigModel.BedCountFilter bedCount = new RoomsListConfigModel.BedCountFilter();
        bedCount.setEnabled(true);
        bedCount.setLabel("Bed Count");
        bedCount.setMin(1);
        bedCount.setMax(4);
        bedCount.setDefaultValue(2);
        return bedCount;
    }

    private RoomsListConfigModel.RoomSizeFilter createValidRoomSizeFilter() {
        RoomsListConfigModel.RoomSizeFilter roomSize = new RoomsListConfigModel.RoomSizeFilter();
        roomSize.setEnabled(true);
        roomSize.setLabel("Room Size");
        roomSize.setMin(20);
        roomSize.setMax(100);
        return roomSize;
    }

    private RoomsListConfigModel.AmenitiesFilter createValidAmenitiesFilter() {
        RoomsListConfigModel.AmenitiesFilter amenities = new RoomsListConfigModel.AmenitiesFilter();
        amenities.setEnabled(true);
        amenities.setLabel("Amenities");
        return amenities;
    }
} 