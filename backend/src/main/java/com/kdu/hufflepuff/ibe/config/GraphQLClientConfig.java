package com.kdu.hufflepuff.ibe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class GraphQLClientConfig {
    @Value("${graphql.api.url}")
    private String graphqlApiUrl;

    @Value("${graphql.api.key}")
    private String graphqlApiKey;

    @Bean
    public GraphQlClient graphQlClient() {
        HttpClient httpClient = HttpClient.create()
            .wiretap(true); // Enable wiretap for detailed logging

        WebClient webClient = WebClient.builder()
            .baseUrl(graphqlApiUrl)
            .defaultHeader("x-api-key", graphqlApiKey)
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();

        return HttpGraphQlClient.builder(webClient)
            .build();
    }
}