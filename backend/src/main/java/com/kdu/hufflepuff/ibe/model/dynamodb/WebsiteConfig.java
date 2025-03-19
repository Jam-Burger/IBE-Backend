package com.kdu.hufflepuff.ibe.model.dynamodb;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Data
@DynamoDbBean
public class WebsiteConfig {
    private String tenantId;
    private String sk;
    private String configType;
    private Long updatedAt;
    private GlobalConfig globalConfig;
    private LandingPageConfig landingPageConfig;

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
    public String getConfigType() {
        return configType;
    }

    @DynamoDbAttribute("UpdatedAt")
    public Long getUpdatedAt() {
        return updatedAt;
    }

    @DynamoDbAttribute("GlobalConfig")
    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    @DynamoDbAttribute("LandingPageConfig")
    public LandingPageConfig getLandingPageConfig() {
        return landingPageConfig;
    }
} 