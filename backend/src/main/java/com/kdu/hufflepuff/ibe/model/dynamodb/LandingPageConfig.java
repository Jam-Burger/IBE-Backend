package com.kdu.hufflepuff.ibe.model.dynamodb;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

import java.util.List;

@Data
@DynamoDbBean
public class LandingPageConfig {
    private String pageTitle;
    private Banner banner;
    private SearchForm searchForm;

    @DynamoDbAttribute("PageTitle")
    public String getPageTitle() {
        return pageTitle;
    }

    @DynamoDbAttribute("Banner")
    public Banner getBanner() {
        return banner;
    }

    @DynamoDbAttribute("SearchForm")
    public SearchForm getSearchForm() {
        return searchForm;
    }

    @Data
    @DynamoDbBean
    public static class Banner {
        private boolean enabled;
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
        private GuestOptions guestOptions;
        private RoomOptions roomOptions;
        private Accessibility accessibility;

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
    public static class GuestOptions {
        private boolean enabled;
        private List<GuestCategory> categories;

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Categories")
        public List<GuestCategory> getCategories() {
            return categories;
        }
    }

    @Data
    @DynamoDbBean
    public static class GuestCategory {
        private String name;
        private boolean enabled;
        private int min;
        private int max;
        private String label;

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

        @DynamoDbAttribute("Label")
        public String getLabel() {
            return label;
        }
    }

    @Data
    @DynamoDbBean
    public static class RoomOptions {
        private boolean enabled;
        private int min;
        private int max;
        private int defaultValue;

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

        @DynamoDbAttribute("DefaultValue")
        public int getDefaultValue() {
            return defaultValue;
        }
    }

    @Data
    @DynamoDbBean
    public static class Accessibility {
        private boolean enabled;
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