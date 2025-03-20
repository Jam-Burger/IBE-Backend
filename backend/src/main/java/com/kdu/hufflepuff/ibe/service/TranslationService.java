package com.kdu.hufflepuff.ibe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class TranslationService {

    @Value("${translation.api.url}")
    private String translationApiUrl;

    private final RestTemplate restTemplate;

    public TranslationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String translateText(String text, String sourceLang, String targetLang) {
        // Prepare the request body
        Map<String, String> requestBody = Map.of(
                "q", text,
                "source", sourceLang,
                "target", targetLang
        );

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create an HTTP entity with the body and headers
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(translationApiUrl, HttpMethod.POST, entity, Map.class);

            // Extract translated text from the response
            if (response.getBody() != null && response.getBody().containsKey("translatedText")) {
                return (String) response.getBody().get("translatedText");
            }
        } catch (Exception e) {
            log.error("Translation API error: {}", e.getMessage());
        }

        return text; // Return original text in case of failure
    }
}

