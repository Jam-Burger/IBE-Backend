package com.kdu.hufflepuff.ibe.model.dynamodb;

import jakarta.validation.constraints.*;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.List;

@Data
@DynamoDbBean
public class CheckoutConfigModel {
    private List<Section> sections;

    @DynamoDbAttribute("Sections")
    public List<Section> getSections() {
        return sections;
    }

    @Data
    @DynamoDbBean
    public static class Section {
        @NotBlank
        private String title;

        @NotBlank
        private String id;

        private boolean enabled;
        private List<Field> fields;

        @DynamoDbAttribute("Title")
        public String getTitle() {
            return title;
        }

        @DynamoDbAttribute("Id")
        public String getId() {
            return id;
        }

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Fields")
        public List<Field> getFields() {
            return fields;
        }
    }

    @Data
    @DynamoDbBean
    public static class Field {
        @NotBlank
        private String label;

        @NotBlank
        private String name;

        @NotBlank
        private String type;

        private boolean required;
        private boolean enabled;
        private String pattern;
        private List<String> options;

        @DynamoDbAttribute("Label")
        public String getLabel() {
            return label;
        }

        @DynamoDbAttribute("Name")
        public String getName() {
            return name;
        }

        @DynamoDbAttribute("Type")
        public String getType() {
            return type;
        }

        @DynamoDbAttribute("Required")
        public boolean isRequired() {
            return required;
        }

        @DynamoDbAttribute("Enabled")
        public boolean isEnabled() {
            return enabled;
        }

        @DynamoDbAttribute("Pattern")
        public String getPattern() {
            return pattern;
        }

        @DynamoDbAttribute("Options")
        public List<String> getOptions() {
            return options;
        }
    }
}
