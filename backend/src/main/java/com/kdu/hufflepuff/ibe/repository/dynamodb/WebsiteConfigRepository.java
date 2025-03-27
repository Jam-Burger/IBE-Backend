package com.kdu.hufflepuff.ibe.repository.dynamodb;

import com.kdu.hufflepuff.ibe.model.dynamodb.WebsiteConfigModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
public class WebsiteConfigRepository {
    private final DynamoDbTable<WebsiteConfigModel> table;

    public WebsiteConfigRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,
        @Value("${aws.dynamodb.table-name}") String tableName) {
        table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(WebsiteConfigModel.class));
    }

    /**
     * Retrieves a configuration by tenant ID and config type.
     *
     * @param pk The tenant ID
     * @param sk The configuration type
     * @return Optional containing the configuration if found
     */
    public Optional<WebsiteConfigModel> getConfig(String pk, String sk) {
        Key key = Key.builder()
            .partitionValue(pk)
            .sortValue(sk)
            .build();

        return Optional.ofNullable(table.getItem(key));
    }

    /**
     * Saves a configuration.
     *
     * @param config The configuration to save
     * @return The saved configuration
     */
    public WebsiteConfigModel saveConfig(WebsiteConfigModel config) {
        table.putItem(config);
        return config;
    }

    /**
     * Deletes a configuration and returns the deleted item.
     *
     * @param pk The tenant ID
     * @param sk The configuration type
     * @return The deleted configuration
     */
    public WebsiteConfigModel deleteConfig(String pk, String sk) {
        Key key = Key.builder()
            .partitionValue(pk)
            .sortValue(sk)
            .build();

        return table.deleteItem(key);
    }
} 