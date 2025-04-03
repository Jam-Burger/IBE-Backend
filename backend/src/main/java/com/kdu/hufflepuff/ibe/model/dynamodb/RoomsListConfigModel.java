package com.kdu.hufflepuff.ibe.model.dynamodb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kdu.hufflepuff.ibe.model.enums.SortOption;
import jakarta.validation.constraints.*;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@Data
@DynamoDbBean
public class RoomsListConfigModel {
    @NotNull(message = "Banner configuration is required")
    private Banner banner;

    @NotNull(message = "Steps configuration is required")
    private Steps steps;

    @NotNull(message = "Filters configuration is required")
    private Filters filters;

    @DynamoDbAttribute("RoomsListBanner")
    public Banner getBanner() {
        return banner;
    }

    @DynamoDbAttribute("Steps")
    public Steps getSteps() {
        return steps;
    }

    @DynamoDbAttribute("Filters")
    public Filters getFilters() {
        return filters;
    }

    @Data
    @DynamoDbBean
    public static class Steps {
        private boolean enabled;

        @NotEmpty(message = "At least one step label must be configured")
        private List<String> labels;

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Labels")
        public List<String> getLabels() {
            return labels;
        }
    }

    @Data
    @DynamoDbBean
    public static class Filters {
        @NotNull(message = "Sort options configuration is required")
        private SortOptions sortOptions;

        @NotNull(message = "Filter groups configuration is required")
        private FilterGroups filterGroups;

        @DynamoDbAttribute("SortOptions")
        public SortOptions getSortOptions() {
            return sortOptions;
        }

        @DynamoDbAttribute("FilterGroups")
        public FilterGroups getFilterGroups() {
            return filterGroups;
        }
    }

    @Data
    @DynamoDbBean
    public static class SortOptions {
        private boolean enabled;

        @NotNull(message = "Default sort option is required")
        private SortOption defaultSort;

        @NotEmpty(message = "At least one sort option must be configured")
        private List<SortOptionConfig> options;

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Default")
        public SortOption getDefaultSort() {
            return defaultSort;
        }

        @DynamoDbAttribute("Options")
        public List<SortOptionConfig> getOptions() {
            return options;
        }
    }

    @Data
    @DynamoDbBean
    public static class SortOptionConfig {
        @NotNull(message = "Sort option value is required")
        private SortOption value;

        @NotBlank(message = "Sort option label is required")
        @Size(max = 50, message = "Sort option label cannot exceed 50 characters")
        private String label;

        private boolean enabled;

        @DynamoDbAttribute("Value")
        public SortOption getValue() {
            return value;
        }

        @DynamoDbAttribute("Label")
        public String getLabel() {
            return label;
        }

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }
    }

    @Data
    @DynamoDbBean
    public static class FilterGroups {
        @NotNull(message = "Ratings filter configuration is required")
        private RatingsFilter ratings;

        @NotNull(message = "Bed types filter configuration is required")
        private BedTypesFilter bedTypes;

        @NotNull(message = "Bed count filter configuration is required")
        private BedCountFilter bedCount;

        @NotNull(message = "Room size filter configuration is required")
        private RoomSizeFilter roomSize;

        @NotNull(message = "Amenities filter configuration is required")
        private AmenitiesFilter amenities;

        @DynamoDbAttribute("Ratings")
        public RatingsFilter getRatings() {
            return ratings;
        }

        @DynamoDbAttribute("BedTypes")
        public BedTypesFilter getBedTypes() {
            return bedTypes;
        }

        @DynamoDbAttribute("BedCount")
        public BedCountFilter getBedCount() {
            return bedCount;
        }

        @DynamoDbAttribute("RoomSize")
        public RoomSizeFilter getRoomSize() {
            return roomSize;
        }

        @DynamoDbAttribute("Amenities")
        public AmenitiesFilter getAmenities() {
            return amenities;
        }
    }

    @Data
    @DynamoDbBean
    public static class RatingsFilter {
        private boolean enabled;

        @NotBlank(message = "Ratings filter label is required")
        @Size(max = 50, message = "Ratings filter label cannot exceed 50 characters")
        private String label;

        @NotEmpty(message = "At least one rating option must be configured")
        private List<RatingOption> options;

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Label")
        public String getLabel() {
            return label;
        }

        @DynamoDbAttribute("Options")
        public List<RatingOption> getOptions() {
            return options;
        }
    }

    @Data
    @DynamoDbBean
    public static class RatingOption {
        @NotNull(message = "Rating value is required")
        private Integer value;

        @NotBlank(message = "Rating label is required")
        @Size(max = 50, message = "Rating label cannot exceed 50 characters")
        private String label;

        private boolean enabled;

        @DynamoDbAttribute("Value")
        public Integer getValue() {
            return value;
        }

        @DynamoDbAttribute("Label")
        public String getLabel() {
            return label;
        }

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }
    }

    @Data
    @DynamoDbBean
    public static class BedTypesFilter {
        private boolean enabled;

        @NotBlank(message = "Bed types filter label is required")
        @Size(max = 50, message = "Bed types filter label cannot exceed 50 characters")
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

    @Data
    @DynamoDbBean
    public static class BedCountFilter {
        private boolean enabled;

        @NotBlank(message = "Bed count filter label is required")
        @Size(max = 50, message = "Bed count filter label cannot exceed 50 characters")
        private String label;

        @Min(value = 1, message = "Minimum bed count must be at least 1")
        private int min;

        @Min(value = 1, message = "Maximum bed count must be at least 1")
        private int max;

        @JsonProperty("default")
        @Min(value = 1, message = "Default bed count must be at least 1")
        private int defaultValue;

        @AssertTrue(message = "Default bed count must be between minimum and maximum values")
        private boolean isValidDefaultValue() {
            return defaultValue >= min && defaultValue <= max;
        }

        @AssertTrue(message = "Maximum bed count must be greater than or equal to minimum value")
        private boolean isValidRange() {
            return max >= min;
        }

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Label")
        public String getLabel() {
            return label;
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
    public static class RoomSizeFilter {
        private boolean enabled;

        @NotBlank(message = "Room size filter label is required")
        @Size(max = 50, message = "Room size filter label cannot exceed 50 characters")
        private String label;

        @Min(value = 1, message = "Minimum room size must be at least 1")
        private int min;

        @Min(value = 1, message = "Maximum room size must be at least 1")
        private int max;

        @AssertTrue(message = "Maximum room size must be greater than or equal to minimum value")
        private boolean isValidRange() {
            return max >= min;
        }

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Label")
        public String getLabel() {
            return label;
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
    public static class AmenitiesFilter {
        private boolean enabled;

        @NotBlank(message = "Amenities filter label is required")
        @Size(max = 50, message = "Amenities filter label cannot exceed 50 characters")
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