package com.kdu.hufflepuff.ibe.model.dynamodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbFlatten;

import java.util.List;

@Data
@DynamoDbBean
public class LandingPageConfigModel {
    @NotBlank(message = "Page title is required")
    @Size(max = 255, message = "Page title cannot exceed 255 characters")
    private String pageTitle;

    @Valid
    @NotNull(message = "Banner configuration is required")
    private Banner banner;

    @Valid
    @NotNull(message = "Search form configuration is required")
    private SearchForm searchForm;

    @DynamoDbAttribute("PageTitle")
    public String getPageTitle() {
        return pageTitle;
    }

    @DynamoDbAttribute("Banner")
    public Banner getBanner() {
        return banner;
    }

    @DynamoDbFlatten
    @DynamoDbAttribute("SearchForm")
    public SearchForm getSearchForm() {
        return searchForm;
    }

    @Data
    @DynamoDbBean
    public static class Banner {
        private boolean enabled;

        @Size(max = 1024, message = "Image URL cannot exceed 1024 characters")
        private String imageUrl;

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("ImageUrl")
        public String getImageUrl() {
            return imageUrl;
        }
    }

    @Data
    @DynamoDbBean
    public static class SearchForm {
        @Valid
        @NotNull(message = "Length of stay configuration is required")
        private LengthOfStay lengthOfStay;

        @Valid
        @NotNull(message = "Guest options configuration is required")
        private GuestOptions guestOptions;

        @Valid
        @NotNull(message = "Room options configuration is required")
        private RoomOptions roomOptions;

        @Valid
        @NotNull(message = "Accessibility configuration is required")
        private Accessibility accessibility;

        @DynamoDbAttribute("LengthOfStay")
        public LengthOfStay getLengthOfStay() {
            return lengthOfStay;
        }

        @DynamoDbAttribute("GuestOptions")
        public GuestOptions getGuestOptions() {
            return guestOptions;
        }

        @DynamoDbAttribute("RoomOptions")
        public RoomOptions getRoomOptions() {
            return roomOptions;
        }

        @DynamoDbAttribute("Accessibility")
        public Accessibility getAccessibility() {
            return accessibility;
        }
    }

    @Data
    @DynamoDbBean
    public static class LengthOfStay {
        @Min(value = 1, message = "Minimum length of stay must be at least 1")
        private int min;

        @Min(value = 1, message = "Maximum length of stay must be at least 1")
        private int max;

        @AssertTrue(message = "Maximum length of stay must be greater than or equal to minimum length of stay")
        private boolean isValidRange() {
            return max >= min;
        }

        @DynamoDbAttribute("Min")
        public int getMin() {
            return min;
        }

        @DynamoDbAttribute("Max")
        public int getMax() {
            return max;
        }
    }

    @Data
    @DynamoDbBean
    public static class GuestOptions {
        private boolean enabled;

        @Min(value = 1, message = "Minimum guest count must be at least 1")
        private int min;

        @Min(value = 1, message = "Maximum guest count must be at least 1")
        private int max;

        @Valid
        @NotEmpty(message = "At least one guest category must be configured")
        private List<GuestCategory> categories;

        @AssertTrue(message = "Maximum guest count must be greater than or equal to minimum guest count")
        private boolean isValidRange() {
            return max >= min;
        }

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Min")
        public int getMin() {
            return min;
        }

        @DynamoDbAttribute("Max")
        public int getMax() {
            return max;
        }

        @DynamoDbAttribute("Categories")
        public List<GuestCategory> getCategories() {
            return categories;
        }
    }

    @Data
    @DynamoDbBean
    public static class GuestCategory {
        @NotBlank(message = "Category name is required")
        private String name;

        private boolean enabled;

        @Min(value = 0, message = "Minimum guest count cannot be negative")
        private int min;

        @Min(value = 1, message = "Maximum guest count must be at least 1")
        private int max;

        @JsonProperty("default")
        @Min(value = 0, message = "Default value cannot be negative")
        private int defaultValue;

        @NotBlank(message = "Category label is required")
        @Size(max = 50, message = "Category label cannot exceed 50 characters")
        private String label;

        @AssertTrue(message = "Default guest count must be between minimum and maximum values")
        private boolean isValidDefaultValue() {
            return defaultValue >= min && defaultValue <= max;
        }

        @AssertTrue(message = "Maximum guest count must be greater than or equal to minimum value")
        private boolean isValidRange() {
            return max >= min;
        }

        @DynamoDbAttribute("Name")
        public String getName() {
            return name;
        }

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Min")
        public int getMin() {
            return min;
        }

        @DynamoDbAttribute("Max")
        public int getMax() {
            return max;
        }

        @DynamoDbAttribute("Default")
        public int getDefaultValue() {
            return defaultValue;
        }

        @DynamoDbAttribute("Label")
        public String getLabel() {
            return label;
        }
    }

    @Data
    @DynamoDbBean
    public static class RoomOptions {
        private boolean enabled;

        @Min(value = 1, message = "Minimum room count must be at least 1")
        private int min;

        @Min(value = 1, message = "Maximum room count must be at least 1")
        private int max;

        @JsonProperty("default")
        @Min(value = 1, message = "Default room count must be at least 1")
        private int defaultValue;

        @AssertTrue(message = "Default room count must be between minimum and maximum values")
        private boolean isValidDefaultValue() {
            return defaultValue >= min && defaultValue <= max;
        }

        @AssertTrue(message = "Maximum room count must be greater than or equal to minimum value")
        private boolean isValidRange() {
            return max >= min;
        }

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Min")
        public int getMin() {
            return min;
        }

        @DynamoDbAttribute("Max")
        public int getMax() {
            return max;
        }

        @DynamoDbAttribute("Default")
        public int getDefaultValue() {
            return defaultValue;
        }
    }

    @Data
    @DynamoDbBean
    public static class Accessibility {
        private boolean enabled;

        @Size(max = 255, message = "Accessibility label cannot exceed 255 characters")
        private String label;

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Label")
        public String getLabel() {
            return label;
        }
    }
} 