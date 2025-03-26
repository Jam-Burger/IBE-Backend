package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.exception.ConfigNotFoundException;
import com.kdu.hufflepuff.ibe.exception.InvalidConfigException;
import com.kdu.hufflepuff.ibe.model.dto.in.ConfigRequestDTO;
import com.kdu.hufflepuff.ibe.model.dynamodb.GlobalConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.LandingPageConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfigModel;
import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import com.kdu.hufflepuff.ibe.repository.dynamodb.WebsiteConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebsiteConfigServiceImplTest {

    @Mock
    private WebsiteConfigRepository configRepository;

    private WebsiteConfigServiceImpl websiteConfigService;

    @BeforeEach
    void setUp() {
        websiteConfigService = new WebsiteConfigServiceImpl(configRepository);
    }

    @Test
    void getConfig_ShouldReturnConfig_WhenConfigExists() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.GLOBAL;
        String pk = "TENANT#1";

        WebsiteConfigModel expectedConfig = new WebsiteConfigModel();
        expectedConfig.setTenantId(pk);
        expectedConfig.setConfigType(configType);
        expectedConfig.setSk(configType.getKey());

        when(configRepository.getConfig(pk, configType.getKey()))
            .thenReturn(Optional.of(expectedConfig));

        WebsiteConfigModel result = websiteConfigService.getConfig(tenantId, configType);

        assertThat(result).isEqualTo(expectedConfig);
        verify(configRepository).getConfig(pk, configType.getKey());
    }

    @Test
    void getConfig_ShouldThrowException_WhenConfigNotFound() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.GLOBAL;
        String pk = "TENANT#1";

        when(configRepository.getConfig(pk, configType.getKey()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> websiteConfigService.getConfig(tenantId, configType))
            .isInstanceOf(ConfigNotFoundException.class);
    }

    @Test
    void saveConfig_ShouldSaveGlobalConfig_WhenValidRequest() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.GLOBAL;
        GlobalConfigModel globalConfig = new GlobalConfigModel();

        ConfigRequestDTO<GlobalConfigModel> request = new ConfigRequestDTO<>();
        request.setConfig(globalConfig);

        WebsiteConfigModel expectedConfig = new WebsiteConfigModel();
        expectedConfig.setTenantId("TENANT#1");
        expectedConfig.setConfigType(configType);
        expectedConfig.setSk(configType.getKey());
        expectedConfig.setGlobalConfigModel(globalConfig);
        expectedConfig.setUpdatedAt(Instant.now().getEpochSecond());

        when(configRepository.saveConfig(any(WebsiteConfigModel.class)))
            .thenReturn(expectedConfig);

        WebsiteConfigModel result = websiteConfigService.saveConfig(tenantId, configType, request);

        assertThat(result).isEqualTo(expectedConfig);
        verify(configRepository).saveConfig(any(WebsiteConfigModel.class));
    }

    @Test
    void saveConfig_ShouldSaveLandingConfig_WhenValidRequest() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.LANDING;
        LandingPageConfigModel landingConfig = new LandingPageConfigModel();

        ConfigRequestDTO<LandingPageConfigModel> request = new ConfigRequestDTO<>();
        request.setConfig(landingConfig);

        WebsiteConfigModel expectedConfig = new WebsiteConfigModel();
        expectedConfig.setTenantId("TENANT#1");
        expectedConfig.setConfigType(configType);
        expectedConfig.setSk(configType.getKey());
        expectedConfig.setLandingPageConfigModel(landingConfig);
        expectedConfig.setUpdatedAt(Instant.now().getEpochSecond());

        when(configRepository.saveConfig(any(WebsiteConfigModel.class)))
            .thenReturn(expectedConfig);

        WebsiteConfigModel result = websiteConfigService.saveConfig(tenantId, configType, request);

        assertThat(result).isEqualTo(expectedConfig);
        verify(configRepository).saveConfig(any(WebsiteConfigModel.class));
    }

    @Test
    void saveConfig_ShouldThrowException_WhenNullRequest() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.GLOBAL;

        assertThatThrownBy(() -> websiteConfigService.saveConfig(tenantId, configType, null))
            .isInstanceOf(InvalidConfigException.class);
    }

    @Test
    void deleteConfig_ShouldDeleteConfig_WhenConfigExists() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.GLOBAL;
        String pk = "TENANT#1";

        WebsiteConfigModel expectedConfig = new WebsiteConfigModel();
        expectedConfig.setTenantId(pk);
        expectedConfig.setConfigType(configType);
        expectedConfig.setSk(configType.getKey());

        when(configRepository.deleteConfig(pk, configType.getKey()))
            .thenReturn(expectedConfig);

        WebsiteConfigModel result = websiteConfigService.deleteConfig(tenantId, configType);
        assertThat(result).isEqualTo(expectedConfig);
        verify(configRepository).deleteConfig(pk, configType.getKey());
    }

    @Test
    void validateInput_ShouldThrowException_WhenTenantIdIsNull() {
        ConfigType configType = ConfigType.GLOBAL;
        assertThatThrownBy(() -> websiteConfigService.getConfig(null, configType))
            .isInstanceOf(InvalidConfigException.class);
    }

    @Test
    void validateInput_ShouldThrowException_WhenConfigTypeIsNull() {
        Long tenantId = 1L;
        assertThatThrownBy(() -> websiteConfigService.getConfig(tenantId, null))
            .isInstanceOf(InvalidConfigException.class);
    }
} 