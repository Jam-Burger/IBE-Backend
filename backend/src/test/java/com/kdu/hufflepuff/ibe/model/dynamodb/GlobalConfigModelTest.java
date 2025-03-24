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

class GlobalConfigModelTest {

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
        GlobalConfigModel model = new GlobalConfigModel();
        GlobalConfigModel.Brand brand = createValidBrand();
        List<GlobalConfigModel.Language> languages = List.of(createValidLanguage());
        List<GlobalConfigModel.Currency> currencies = List.of(createValidCurrency());
        List<Long> properties = List.of(1L);

        // When
        model.setBrand(brand);
        model.setLanguages(languages);
        model.setCurrencies(currencies);
        model.setProperties(properties);

        // Then
        assertThat(model.getBrand()).isEqualTo(brand);
        assertThat(model.getLanguages()).isEqualTo(languages);
        assertThat(model.getCurrencies()).isEqualTo(currencies);
        assertThat(model.getProperties()).isEqualTo(properties);
    }

    @Test
    void testDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<GlobalConfigModel> clazz = GlobalConfigModel.class;

        // Then
        Method getBrand = clazz.getMethod("getBrand");
        assertThat(getBrand.isAnnotationPresent(DynamoDbFlatten.class)).isTrue();
        assertThat(getBrand.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Brand");

        Method getLanguages = clazz.getMethod("getLanguages");
        assertThat(getLanguages.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Languages");

        Method getCurrencies = clazz.getMethod("getCurrencies");
        assertThat(getCurrencies.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Currencies");

        Method getProperties = clazz.getMethod("getProperties");
        assertThat(getProperties.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("Properties");
    }

    @Test
    void testValidation_ValidModel() {
        // Given
        GlobalConfigModel model = createValidGlobalConfig();

        // When
        Set<ConstraintViolation<GlobalConfigModel>> violations = validator.validate(model);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidation_InvalidModel() {
        // Given
        GlobalConfigModel model = new GlobalConfigModel();

        // When
        Set<ConstraintViolation<GlobalConfigModel>> violations = validator.validate(model);

        // Then
        assertThat(violations).hasSize(4);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Brand configuration is required",
                "At least one language must be configured",
                "At least one currency must be configured",
                "At least one property must be configured"
            );
    }

    @Test
    void testBrandValidation() {
        // Given
        GlobalConfigModel.Brand brand = new GlobalConfigModel.Brand();

        // When
        Set<ConstraintViolation<GlobalConfigModel.Brand>> violations = validator.validate(brand);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Logo URL is required",
                "Company name is required"
            );
    }

    @Test
    void testLanguageValidation() {
        // Given
        GlobalConfigModel.Language language = new GlobalConfigModel.Language();

        // When
        Set<ConstraintViolation<GlobalConfigModel.Language>> violations = validator.validate(language);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Language code is required",
                "Language name is required"
            );
    }

    @Test
    void testCurrencyValidation() {
        // Given
        GlobalConfigModel.Currency currency = new GlobalConfigModel.Currency();

        // When
        Set<ConstraintViolation<GlobalConfigModel.Currency>> violations = validator.validate(currency);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations).extracting("message")
            .containsExactlyInAnyOrder(
                "Currency code is required",
                "Currency symbol is required"
            );
    }

    private GlobalConfigModel createValidGlobalConfig() {
        GlobalConfigModel model = new GlobalConfigModel();
        model.setBrand(createValidBrand());
        model.setLanguages(List.of(createValidLanguage()));
        model.setCurrencies(List.of(createValidCurrency()));
        model.setProperties(List.of(1L));
        return model;
    }

    private GlobalConfigModel.Brand createValidBrand() {
        GlobalConfigModel.Brand brand = new GlobalConfigModel.Brand();
        brand.setLogoUrl("https://example.com/logo.png");
        brand.setCompanyName("Test Company");
        return brand;
    }

    private GlobalConfigModel.Language createValidLanguage() {
        GlobalConfigModel.Language language = new GlobalConfigModel.Language();
        language.setCode("en");
        language.setName("English");
        return language;
    }

    private GlobalConfigModel.Currency createValidCurrency() {
        GlobalConfigModel.Currency currency = new GlobalConfigModel.Currency();
        currency.setCode("USD");
        currency.setSymbol("$");
        return currency;
    }
} 