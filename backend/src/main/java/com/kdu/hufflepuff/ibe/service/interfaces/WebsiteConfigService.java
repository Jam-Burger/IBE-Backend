package com.kdu.hufflepuff.ibe.service.interfaces;

import com.kdu.hufflepuff.ibe.exception.ConfigNotFoundException;
import com.kdu.hufflepuff.ibe.exception.InvalidConfigException;
import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfigModel;
import com.kdu.hufflepuff.ibe.model.enums.ConfigType;

/**
 * Service interface for managing website configurations.
 * Handles CRUD operations for different types of configurations.
 */
public interface WebsiteConfigService {
    /**
     * Retrieves a configuration by tenant ID and config type.
     *
     * @param tenantId   The tenant ID to retrieve configuration for
     * @param configType The type of configuration to retrieve
     * @return The website configuration
     * @throws ConfigNotFoundException if the configuration is not found
     * @throws InvalidConfigException  if the tenant ID or config type is invalid
     */
    WebsiteConfigModel getConfig(Long tenantId, ConfigType configType);

    /**
     * Saves a configuration for a tenant.
     *
     * @param tenantId      The tenant ID to save configuration for
     * @param configType    The type of configuration to save
     * @param configRequest The configuration data transfer object containing the configuration
     * @param <T>          The type parameter for the configuration class
     * @return The saved website configuration
     * @throws InvalidConfigException if any of the parameters are invalid or if the config type is unsupported
     */
    <T> WebsiteConfigModel saveConfig(Long tenantId, ConfigType configType, ConfigRequestDTO<T> configRequest);

    /**
     * Deletes a configuration for a tenant.
     *
     * @param tenantId   The tenant ID to delete configuration for
     * @param configType The type of configuration to delete
     * @return The deleted website configuration
     * @throws ConfigNotFoundException if the configuration is not found
     * @throws InvalidConfigException if the tenant ID or config type is invalid
     */
    WebsiteConfigModel deleteConfig(Long tenantId, ConfigType configType);
} 