package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.GlobalConfig;
import com.kdu.hufflepuff.ibe.model.dynamodb.LandingPageConfig;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfig;
import com.kdu.hufflepuff.ibe.repository.dynamodb.WebsiteConfigRepository;
import com.kdu.hufflepuff.ibe.service.WebsiteConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class WebsiteConfigServiceImpl implements WebsiteConfigService {
    private final WebsiteConfigRepository configRepository;

    public WebsiteConfigServiceImpl(WebsiteConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WebsiteConfig> getConfig(String tenantId, String configType) {
        String sk = computeSortKey(configType);
        return configRepository.getConfig(tenantId, sk);
    }

    @Override
    public <T> void saveConfig(String tenantId, String configType, ConfigRequestDTO<T> configRequest, Class<T> configClass) {
        // Validate inputs
        if (tenantId == null || tenantId.trim().isEmpty()) {
            throw new IllegalArgumentException("TenantId cannot be null or empty");
        }
        if (configType == null || configType.trim().isEmpty()) {
            throw new IllegalArgumentException("ConfigType cannot be null or empty");
        }
        if (configRequest == null || configRequest.getConfig() == null) {
            throw new IllegalArgumentException("Config data cannot be null");
        }

        // Create and populate WebsiteConfig
        WebsiteConfig config = new WebsiteConfig();
        config.setTenantId(tenantId);
        config.setConfigType(configType);
        config.setSk(computeSortKey(configType));
        config.setUpdatedAt(Instant.now().getEpochSecond());

        // Set the appropriate config based on type
        T configData = configRequest.getConfig();
        if (configClass == GlobalConfig.class) {
            config.setGlobalConfig((GlobalConfig) configData);
        } else if (configClass == LandingPageConfig.class) {
            config.setLandingPageConfig((LandingPageConfig) configData);
        } else {
            throw new IllegalArgumentException("Unsupported config type: " + configClass.getSimpleName());
        }

        configRepository.saveConfig(config);
    }

    @Override
    public void deleteConfig(String tenantId, String configType) {
        String sk = computeSortKey(configType);
        configRepository.deleteConfig(tenantId, sk);
    }

    private String computeSortKey(String configType) {
        return "CONFIG#" + configType.toUpperCase();
    }
} 