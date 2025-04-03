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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebsiteConfigServiceImplTest {

    @Mock
    private WebsiteConfigRepository configRepository;

    @Captor
    private ArgumentCaptor<WebsiteConfigModel> configCaptor;

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
            .isInstanceOf(ConfigNotFoundException.class)
            .hasMessageContaining(pk)
            .hasMessageContaining(configType.getKey());
    }

    @Test
    void saveConfig_ShouldSaveGlobalConfig_WhenValidRequest() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.GLOBAL;
        GlobalConfigModel globalConfig = createGlobalConfig();

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

        verify(configRepository).saveConfig(configCaptor.capture());
        WebsiteConfigModel capturedConfig = configCaptor.getValue();
        assertThat(capturedConfig.getTenantId()).isEqualTo("TENANT#1");
        assertThat(capturedConfig.getConfigType()).isEqualTo(configType);
        assertThat(capturedConfig.getSk()).isEqualTo(configType.getKey());
        assertThat(capturedConfig.getGlobalConfigModel()).isEqualTo(globalConfig);
        assertThat(capturedConfig.getUpdatedAt()).isNotNull();
    }

    @Test
    void saveConfig_ShouldSaveLandingConfig_WhenValidRequest() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.LANDING;
        LandingPageConfigModel landingConfig = createLandingConfig();

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

        verify(configRepository).saveConfig(configCaptor.capture());
        WebsiteConfigModel capturedConfig = configCaptor.getValue();
        assertThat(capturedConfig.getTenantId()).isEqualTo("TENANT#1");
        assertThat(capturedConfig.getConfigType()).isEqualTo(configType);
        assertThat(capturedConfig.getSk()).isEqualTo(configType.getKey());
        assertThat(capturedConfig.getLandingPageConfigModel()).isEqualTo(landingConfig);
        assertThat(capturedConfig.getUpdatedAt()).isNotNull();
    }

    @Test
    void saveConfig_ShouldSaveRoomsListConfig_WhenValidRequest() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.ROOMS_LIST;
        RoomsListConfigModel roomsListConfig = createRoomsListConfig();

        ConfigRequestDTO<RoomsListConfigModel> request = new ConfigRequestDTO<>();
        request.setConfig(roomsListConfig);

        WebsiteConfigModel expectedConfig = new WebsiteConfigModel();
        expectedConfig.setTenantId("TENANT#1");
        expectedConfig.setConfigType(configType);
        expectedConfig.setSk(configType.getKey());
        expectedConfig.setRoomsListConfigModel(roomsListConfig);
        expectedConfig.setUpdatedAt(Instant.now().getEpochSecond());

        when(configRepository.saveConfig(any(WebsiteConfigModel.class)))
            .thenReturn(expectedConfig);

        WebsiteConfigModel result = websiteConfigService.saveConfig(tenantId, configType, request);

        assertThat(result).isEqualTo(expectedConfig);

        verify(configRepository).saveConfig(configCaptor.capture());
        WebsiteConfigModel capturedConfig = configCaptor.getValue();
        assertThat(capturedConfig.getTenantId()).isEqualTo("TENANT#1");
        assertThat(capturedConfig.getConfigType()).isEqualTo(configType);
        assertThat(capturedConfig.getSk()).isEqualTo(configType.getKey());
        assertThat(capturedConfig.getRoomsListConfigModel()).isEqualTo(roomsListConfig);
        assertThat(capturedConfig.getUpdatedAt()).isNotNull();
    }

    @Test
    void saveConfig_ShouldThrowException_WhenNullRequest() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.GLOBAL;

        assertThatThrownBy(() -> websiteConfigService.saveConfig(tenantId, configType, null))
            .isInstanceOf(InvalidConfigException.class)
            .hasMessageContaining("Config data cannot be null");

        verify(configRepository, never()).saveConfig(any());
    }

    @Test
    void saveConfig_ShouldThrowException_WhenNullConfigData() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.GLOBAL;
        ConfigRequestDTO<GlobalConfigModel> request = new ConfigRequestDTO<>();
        // config is null

        assertThatThrownBy(() -> websiteConfigService.saveConfig(tenantId, configType, request))
            .isInstanceOf(InvalidConfigException.class)
            .hasMessageContaining("Config data cannot be null");

        verify(configRepository, never()).saveConfig(any());
    }

    @Test
    void saveConfig_ShouldThrowException_WhenUnsupportedConfigType() {
        Long tenantId = 1L;
        ConfigType configType = null; // Unsupported config type
        GlobalConfigModel globalConfig = createGlobalConfig();

        ConfigRequestDTO<GlobalConfigModel> request = new ConfigRequestDTO<>();
        request.setConfig(globalConfig);

        assertThatThrownBy(() -> websiteConfigService.saveConfig(tenantId, configType, request))
            .isInstanceOf(InvalidConfigException.class)
            .hasMessageContaining("ConfigType cannot be null or empty");

        verify(configRepository, never()).saveConfig(any());
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
            .isInstanceOf(InvalidConfigException.class)
            .hasMessageContaining("TenantId cannot be null or empty");

        verify(configRepository, never()).getConfig(anyString(), anyString());
    }

    @Test
    void validateInput_ShouldThrowException_WhenConfigTypeIsNull() {
        Long tenantId = 1L;
        assertThatThrownBy(() -> websiteConfigService.getConfig(tenantId, null))
            .isInstanceOf(InvalidConfigException.class)
            .hasMessageContaining("ConfigType cannot be null or empty");

        verify(configRepository, never()).getConfig(anyString(), anyString());
    }

    @Test
    void getPk_ShouldReturnFormattedTenantId() {
        Long tenantId = 123L;

        String result = websiteConfigService.getPk(tenantId);

        assertThat(result).isEqualTo("TENANT#123");
    }

    @Test
    void saveConfig_ShouldNotSaveInvalidConfigs() {
        Long tenantId = 1L;
        ConfigType configType = ConfigType.GLOBAL;

        Object invalidObject = new Object(); // Not a valid config type
        ConfigRequestDTO<Object> invalidRequest = new ConfigRequestDTO<>();
        invalidRequest.setConfig(invalidObject);

        assertThatThrownBy(() -> websiteConfigService.saveConfig(tenantId, configType, invalidRequest))
            .isInstanceOf(ClassCastException.class);

        verify(configRepository, never()).saveConfig(any());
    }

    private GlobalConfigModel createGlobalConfig() {
        GlobalConfigModel globalConfig = new GlobalConfigModel();
        GlobalConfigModel.Brand brand = new GlobalConfigModel.Brand();
        brand.setLogoUrl("https://example.com/logo.png");
        brand.setCompanyName("Test Company");
        brand.setPageTitle("Test Page");
        globalConfig.setBrand(brand);
        return globalConfig;
    }

    private LandingPageConfigModel createLandingConfig() {
        return new LandingPageConfigModel();
    }

    private RoomsListConfigModel createRoomsListConfig() {
        return new RoomsListConfigModel();
    }
} 