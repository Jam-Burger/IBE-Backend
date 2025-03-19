package com.kdu.hufflepuff.ibe.service;

import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfig;

import java.util.Optional;

public interface WebsiteConfigService {
    Optional<WebsiteConfig> getConfig(String tenantId, String configType);
    <T> void saveConfig(String tenantId, String configType, ConfigRequestDTO<T> configRequest, Class<T> configClass);
    void deleteConfig(String tenantId, String configType);
} 