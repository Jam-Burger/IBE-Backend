package com.kdu.hufflepuff.ibe.config;

import com.amazonaws.xray.interceptors.TracingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Import(XRayConfig.class)
@ComponentScan(basePackages = {
    "com.kdu.hufflepuff.ibe.repository.dynamodb",
    "com.kdu.hufflepuff.ibe.model.dynamodb"
})
public class AwsConfig {
    private final DefaultCredentialsProvider credentialsProvider = DefaultCredentialsProvider.create();

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public ClientOverrideConfiguration tracingConfig() {
        return ClientOverrideConfiguration.builder()
            .addExecutionInterceptor(new TracingInterceptor())
            .build();
    }

    @Bean
    public S3Client s3Client(ClientOverrideConfiguration tracingConfig) {
        return S3Client.builder()
            .region(Region.of(awsRegion))
            .overrideConfiguration(tracingConfig)
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient(ClientOverrideConfiguration tracingConfig) {
        return DynamoDbClient.builder()
            .region(Region.of(awsRegion))
            .overrideConfiguration(tracingConfig)
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
