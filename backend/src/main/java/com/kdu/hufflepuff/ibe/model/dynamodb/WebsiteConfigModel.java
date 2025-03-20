package com.kdu.hufflepuff.ibe.model.dynamodb;

import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@DynamoDbBean
public class WebsiteConfigModel {
    private String tenantId;
    private String sk;
    private ConfigType configType;
    private Long updatedAt;
    private GlobalConfigModel globalConfigModel;
    private LandingPageConfigModel landingPageConfigModel;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("TenantId")
    public String getTenantId() {
        return tenantId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return sk;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "ConfigTypeIndex")
    @DynamoDbAttribute("ConfigType")
    public ConfigType getConfigType() {
        return configType;
    }

    @DynamoDbAttribute("UpdatedAt")
    public Long getUpdatedAt() {
        return updatedAt;
    }

    @DynamoDbFlatten
    @DynamoDbAttribute("GlobalConfig")
    public GlobalConfigModel getGlobalConfigModel() {
        return globalConfigModel;
    }

    @DynamoDbFlatten
    @DynamoDbAttribute("LandingPageConfig")
    public LandingPageConfigModel getLandingPageConfigModel() {
        return landingPageConfigModel;
    }
} 