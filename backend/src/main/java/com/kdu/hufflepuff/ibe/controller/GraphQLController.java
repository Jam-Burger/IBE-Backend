package com.kdu.hufflepuff.ibe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequestMapping("/api/v1/graphql")
@RequiredArgsConstructor
public class GraphQLController {

    private final RestTemplate restTemplate;
    @Value("${graphql.api.url}")
    private String graphqlApiUrl;
    @Value("${graphql.api.key}")
    private String apiKey;

    @PostMapping
    public ResponseEntity<String> forwardGraphQL(@RequestBody String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("X-Api-Key", apiKey);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(graphqlApiUrl, request, String.class);
    }
}