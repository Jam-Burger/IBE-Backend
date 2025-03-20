package com.kdu.hufflepuff.ibe.model.dynamodb;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbFlatten;

import java.util.List;

@Data
@DynamoDbBean
public class GlobalConfigModel {
    private Brand brand;
    private List<Language> languages;
    private List<Currency> currencies;
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
        private String logoUrl;
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
        private String code;
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
        private String code;
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