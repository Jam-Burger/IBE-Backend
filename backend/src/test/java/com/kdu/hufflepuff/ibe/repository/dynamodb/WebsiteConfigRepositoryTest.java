package com.kdu.hufflepuff.ibe.repository.dynamodb;

import com.kdu.hufflepuff.ibe.model.dynamodb.GlobalConfigModel;
import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfigModel;
import com.kdu.hufflepuff.ibe.model.enums.ConfigType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebsiteConfigRepositoryTest {

    private static final String TABLE_NAME = "test-table";
    @Mock
    private DynamoDbEnhancedClient dynamoDbEnhancedClient;
    @Mock
    private DynamoDbTable<WebsiteConfigModel> dynamoDbTable;
    private WebsiteConfigRepository repository;

    @BeforeEach
    void setUp() {
        when(dynamoDbEnhancedClient.table(anyString(), any(TableSchema.class))).thenReturn(dynamoDbTable);
        repository = new WebsiteConfigRepository(dynamoDbEnhancedClient, TABLE_NAME);
    }

    @Test
    void getConfig_ShouldReturnConfig_WhenExists() {
        // Given
        String pk = "TENANT#1";
        String sk = "CONFIG#GLOBAL";
        WebsiteConfigModel expectedConfig = createConfig(pk, sk, ConfigType.GLOBAL);

        Key key = Key.builder()
            .partitionValue(pk)
            .sortValue(sk)
            .build();

        when(dynamoDbTable.getItem(key)).thenReturn(expectedConfig);

        // When
        Optional<WebsiteConfigModel> result = repository.getConfig(pk, sk);

        // Then
        assertThat(result)
            .isPresent()
            .contains(expectedConfig);
        verify(dynamoDbTable).getItem(key);
    }

    @Test
    void getConfig_ShouldReturnEmpty_WhenNotExists() {
        // Given
        String pk = "TENANT#1";
        String sk = "CONFIG#GLOBAL";

        Key key = Key.builder()
            .partitionValue(pk)
            .sortValue(sk)
            .build();

        when(dynamoDbTable.getItem(key)).thenReturn(null);

        // When
        Optional<WebsiteConfigModel> result = repository.getConfig(pk, sk);

        // Then
        assertThat(result).isEmpty();
        verify(dynamoDbTable).getItem(key);
    }

    @Test
    void saveConfig_ShouldSaveAndReturnConfig() {
        // Given
        String pk = "TENANT#1";
        String sk = "CONFIG#GLOBAL";
        WebsiteConfigModel config = createConfig(pk, sk, ConfigType.GLOBAL);

        // When
        WebsiteConfigModel result = repository.saveConfig(config);

        // Then
        assertThat(result).isEqualTo(config);
        verify(dynamoDbTable).putItem(config);
    }

    @Test
    void deleteConfig_ShouldDeleteAndReturnConfig() {
        // Given
        String pk = "TENANT#1";
        String sk = "CONFIG#GLOBAL";
        WebsiteConfigModel expectedConfig = createConfig(pk, sk, ConfigType.GLOBAL);

        Key key = Key.builder()
            .partitionValue(pk)
            .sortValue(sk)
            .build();

        when(dynamoDbTable.deleteItem(key)).thenReturn(expectedConfig);

        // When
        WebsiteConfigModel result = repository.deleteConfig(pk, sk);

        // Then
        assertThat(result).isEqualTo(expectedConfig);
        verify(dynamoDbTable).deleteItem(key);
    }

    private WebsiteConfigModel createConfig(String pk, String sk, ConfigType configType) {
        WebsiteConfigModel config = new WebsiteConfigModel();
        config.setTenantId(pk);
        config.setSk(sk);
        config.setConfigType(configType);
        config.setUpdatedAt(System.currentTimeMillis());

        // Add a sample GlobalConfigModel
        if (configType == ConfigType.GLOBAL) {
            GlobalConfigModel globalConfig = new GlobalConfigModel();
            GlobalConfigModel.Brand brand = new GlobalConfigModel.Brand();
            brand.setCompanyName("Test Company");
            brand.setLogoUrl("https://example.com/logo.png");
            brand.setPageTitle("Test Page");
            globalConfig.setBrand(brand);
            config.setGlobalConfigModel(globalConfig);
        }

        return config;
    }
} 