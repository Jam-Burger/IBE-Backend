package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ConfigNotFoundException;
import com.kdu.hufflepuff.ibe.exception.InvalidConfigException;
import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.GlobalConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.LandingPageConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfigModel;
import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import com.kdu.hufflepuff.ibe.repository.dynamodb.WebsiteConfigRepository;
import com.kdu.hufflepuff.ibe.service.interfaces.WebsiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WebsiteConfigServiceImpl implements WebsiteConfigService {
    private final WebsiteConfigRepository configRepository;

    @Override
    @Transactional(readOnly = true)
    public WebsiteConfigModel getConfig(String tenantId, ConfigType configType) {
        validateInput(tenantId, configType);
        return configRepository.getConfig(tenantId, configType.getKey())
            .orElseThrow(() -> new ConfigNotFoundException(tenantId, configType.getKey()));
    }

    @Override
    public <T> WebsiteConfigModel saveConfig(String tenantId, ConfigType configType, ConfigRequestDTO<T> configRequest, Class<T> configClass) {
        validateInput(tenantId, configType);
        if (configRequest == null || configRequest.getConfig() == null) {
            throw InvalidConfigException.nullConfigData();
        }

        WebsiteConfigModel config = new WebsiteConfigModel();
        config.setTenantId(tenantId);
        config.setConfigType(configType);
        config.setSk(configType.getKey());
        config.setUpdatedAt(Instant.now().getEpochSecond());

        T configData = configRequest.getConfig();
        if (configClass == GlobalConfigModel.class) {
            config.setGlobalConfigModel((GlobalConfigModel) configData);
        } else if (configClass == LandingPageConfigModel.class) {
            config.setLandingPageConfigModel((LandingPageConfigModel) configData);
        } else {
            throw InvalidConfigException.unsupportedConfigType(configClass.getSimpleName());
        }

        return configRepository.saveConfig(config);
    }

    @Override
    public WebsiteConfigModel deleteConfig(String tenantId, ConfigType configType) {
        validateInput(tenantId, configType);
        return configRepository.deleteConfig(tenantId, configType.getKey());
    }

    private void validateInput(String tenantId, ConfigType configType) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw InvalidConfigException.nullTenantId();
        }
        if (configType == null || configType.getKey().trim().isEmpty()) {
            throw InvalidConfigException.nullConfigType();
        }
    }
} 