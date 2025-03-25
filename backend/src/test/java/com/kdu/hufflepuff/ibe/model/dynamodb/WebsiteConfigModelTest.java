package com.kdu.hufflepuff.ibe.model.dynamodb;

import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.lang.reflect.Method;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class WebsiteConfigModelTest {

    @Test
    void testGettersAndSetters() {
        // Given
        WebsiteConfigModel model = new WebsiteConfigModel();
        String tenantId = "TENANT#1";
        String sk = "CONFIG#GLOBAL";
        ConfigType configType = ConfigType.GLOBAL;
        Long updatedAt = Instant.now().getEpochSecond();
        GlobalConfigModel globalConfig = new GlobalConfigModel();
        LandingPageConfigModel landingConfig = new LandingPageConfigModel();

        // When
        model.setTenantId(tenantId);
        model.setSk(sk);
        model.setConfigType(configType);
        model.setUpdatedAt(updatedAt);
        model.setGlobalConfigModel(globalConfig);
        model.setLandingPageConfigModel(landingConfig);

        // Then
        assertThat(model.getTenantId()).isEqualTo(tenantId);
        assertThat(model.getSk()).isEqualTo(sk);
        assertThat(model.getConfigType()).isEqualTo(configType);
        assertThat(model.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(model.getGlobalConfigModel()).isEqualTo(globalConfig);
        assertThat(model.getLandingPageConfigModel()).isEqualTo(landingConfig);
    }

    @Test
    void testDynamoDbAnnotations() throws NoSuchMethodException {
        // Given
        Class<WebsiteConfigModel> clazz = WebsiteConfigModel.class;

        // Then
        Method getTenantId = clazz.getMethod("getTenantId");
        assertThat(getTenantId.isAnnotationPresent(DynamoDbPartitionKey.class)).isTrue();
        assertThat(getTenantId.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("TenantId");

        Method getSk = clazz.getMethod("getSk");
        assertThat(getSk.isAnnotationPresent(DynamoDbSortKey.class)).isTrue();
        assertThat(getSk.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("SK");

        Method getConfigType = clazz.getMethod("getConfigType");
        assertThat(getConfigType.isAnnotationPresent(DynamoDbSecondaryPartitionKey.class)).isTrue();
        assertThat(getConfigType.getAnnotation(DynamoDbSecondaryPartitionKey.class).indexNames())
            .contains("ConfigTypeIndex");
        assertThat(getConfigType.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("ConfigType");

        Method getUpdatedAt = clazz.getMethod("getUpdatedAt");
        assertThat(getUpdatedAt.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("UpdatedAt");

        Method getGlobalConfig = clazz.getMethod("getGlobalConfigModel");
        assertThat(getGlobalConfig.isAnnotationPresent(DynamoDbFlatten.class)).isTrue();
        assertThat(getGlobalConfig.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("GlobalConfig");

        Method getLandingConfig = clazz.getMethod("getLandingPageConfigModel");
        assertThat(getLandingConfig.isAnnotationPresent(DynamoDbFlatten.class)).isTrue();
        assertThat(getLandingConfig.getAnnotation(DynamoDbAttribute.class).value()).isEqualTo("LandingPageConfig");
    }
} 