package com.kdu.hufflepuff.ibe.model.dynamodb;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbFlatten;

import java.util.List;

@Data
@DynamoDbBean
public class GlobalConfigModel {
    @Valid
    @NotNull(message = "Brand configuration is required")
    private Brand brand;

    @NotEmpty(message = "At least one language must be configured")
    @Valid
    private List<Language> languages;

    @NotEmpty(message = "At least one currency must be configured")
    @Valid
    private List<Currency> currencies;

    @NotEmpty(message = "At least one property must be configured")
    private List<String> properties;

    @DynamoDbFlatten
    @DynamoDbAttribute("Brand")
    public Brand getBrand() {
        return brand;
    }

    @DynamoDbAttribute("Languages")
    public List<Language> getLanguages() {
        return languages;
    }

    @DynamoDbAttribute("Currencies")
    public List<Currency> getCurrencies() {
        return currencies;
    }

    @DynamoDbAttribute("Properties")
    public List<String> getProperties() {
        return properties;
    }

    @Data
    @DynamoDbBean
    public static class Brand {
        @NotBlank(message = "Logo URL is required")
        @Size(max = 1024, message = "Logo URL cannot exceed 1024 characters")
        private String logoUrl;

        @NotBlank(message = "Company name is required")
        @Size(max = 255, message = "Company name cannot exceed 255 characters")
        private String companyName;

        @DynamoDbAttribute("LogoUrl")
        public String getLogoUrl() {
            return logoUrl;
        }

        @DynamoDbAttribute("CompanyName")
        public String getCompanyName() {
            return companyName;
        }
    }

    @Data
    @DynamoDbBean
    public static class Language {
        @NotBlank(message = "Language code is required")
        @Size(min = 2, max = 5, message = "Language code must be between 2 and 5 characters")
        private String code;

        @NotBlank(message = "Language name is required")
        @Size(max = 50, message = "Language name cannot exceed 50 characters")
        private String name;

        @DynamoDbAttribute("Code")
        public String getCode() {
            return code;
        }

        @DynamoDbAttribute("Name")
        public String getName() {
            return name;
        }
    }

    @Data
    @DynamoDbBean
    public static class Currency {
        @NotBlank(message = "Currency code is required")
        @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
        private String code;

        @NotBlank(message = "Currency symbol is required")
        private String symbol;

        @DynamoDbAttribute("Code")
        public String getCode() {
            return code;
        }

        @DynamoDbAttribute("Symbol")
        public String getSymbol() {
            return symbol;
        }
    }
} 