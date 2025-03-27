package com.kdu.hufflepuff.ibe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ComponentScan(basePackages = {
    "com.kdu.hufflepuff.ibe.repository.dynamodb",
    "com.kdu.hufflepuff.ibe.model.dynamodb"
})
public class AwsConfig {
    @Value("${aws.region}")
    private String awsRegion;

    private final DefaultCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();
    private final Region awsRegionRegion = Region.of(awsRegion);

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(awsRegionRegion)
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
            .region(awsRegionRegion)
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();
    }
}
