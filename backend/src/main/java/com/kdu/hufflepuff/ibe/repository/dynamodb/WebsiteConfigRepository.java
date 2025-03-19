package com.kdu.hufflepuff.ibe.repository.dynamodb;

import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
public class WebsiteConfigRepository {
    private final DynamoDbTable<WebsiteConfig> table;

    public WebsiteConfigRepository(
            DynamoDbEnhancedClient dynamoDbEnhancedClient,
            @Value("${aws.dynamodb.table-name}") String tableName) {
        this.table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(WebsiteConfig.class));
    }

    public Optional<WebsiteConfig> getConfig(String tenantId, String configType) {
        Key key = Key.builder()
                .partitionValue(tenantId)
                .sortValue(configType)
                .build();

        return Optional.ofNullable(table.getItem(key));
    }

    public void saveConfig(WebsiteConfig config) {
        table.putItem(config);
    }

    public void deleteConfig(String tenantId, String configType) {
        Key key = Key.builder()
                .partitionValue(tenantId)
                .sortValue(configType)
                .build();

        table.deleteItem(key);
    }
} 