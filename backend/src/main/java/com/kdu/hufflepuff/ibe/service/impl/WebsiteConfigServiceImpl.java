package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ConfigNotFoundException;
import com.kdu.hufflepuff.ibe.exception.InvalidConfigException;
import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.GlobalConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.LandingPageConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.RoomsListConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfigModel;
import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import com.kdu.hufflepuff.ibe.repository.dynamodb.WebsiteConfigRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.WebsiteConfigService;
import com.kdu.hufflepuff.ibe.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebsiteConfigServiceImpl implements WebsiteConfigService {
    private final WebsiteConfigRepository configRepository;

    @Override
    @Transactional(readOnly = true)
    public WebsiteConfigModel getConfig(Long tenantId, ConfigType configType) {
        validateInput(tenantId, configType);
        return configRepository.getConfig(getPk(tenantId), configType.getKey())
            .orElseThrow(() -> new ConfigNotFoundException(getPk(tenantId), configType.getKey()));
    }

    @Override
    public <T> WebsiteConfigModel saveConfig(Long tenantId, ConfigType configType, ConfigRequestDTO<T> configRequest) {
        validateInput(tenantId, configType);
        if (configRequest == null || configRequest.getConfig() == null) {
            throw InvalidConfigException.nullConfigData();
        }

        WebsiteConfigModel config = new WebsiteConfigModel();
        config.setTenantId(getPk(tenantId));
        config.setConfigType(configType);
        config.setSk(configType.getKey());
        config.setUpdatedAt(Instant.now().getEpochSecond());

        T configData = configRequest.getConfig();
        switch (configType) {
            case GLOBAL:
                config.setGlobalConfigModel((GlobalConfigModel) configData);
                break;
            case LANDING:
                config.setLandingPageConfigModel((LandingPageConfigModel) configData);
                break;
            case ROOMS_LIST:
                config.setRoomsListConfigModel((RoomsListConfigModel) configData);
                break;
            default:
                throw InvalidConfigException.unsupportedConfigType(configType.getKey());
        }

        WebsiteConfigModel savedConfig = configRepository.saveConfig(config);

        // Log business event for configuration update with builder pattern
        LoggingUtil.event("ConfigurationSaved")
            .field("tenantId", tenantId)
            .field("configType", configType.name())
            .field("updatedAt", config.getUpdatedAt())
            .log(log);

        return savedConfig;
    }

    @Override
    public WebsiteConfigModel deleteConfig(Long tenantId, ConfigType configType) {
        validateInput(tenantId, configType);
        WebsiteConfigModel deletedConfig = configRepository.deleteConfig(getPk(tenantId), configType.getKey());

        // Log business event for configuration deletion with builder pattern
        LoggingUtil.event("ConfigurationDeleted")
            .field("tenantId", tenantId)
            .field("configType", configType.name())
            .log(log);

        return deletedConfig;
    }

    private void validateInput(Long tenantId, ConfigType configType) {
        if (tenantId == null) {
            throw InvalidConfigException.nullTenantId();
        }
        if (configType == null || configType.getKey().trim().isEmpty()) {
            throw InvalidConfigException.nullConfigType();
        }
    }

    String getPk(Long tenantId) {
        return "TENANT#" + tenantId;
    }
} 