package com.kdu.hufflepuff.ibe.service.impl;



import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import java.util.Map;

@Service
public class TranslationService {

    private final RestTemplate restTemplate;

    public TranslationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String translateText(String text, String sourceLang, String targetLang) {
        String apiUrl = "http://localhost:5000/translate";

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
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            // Extract translated text from the response
            if (response.getBody() != null && response.getBody().containsKey("translatedText")) {
                return response.getBody().get("translatedText").toString();
            }
        } catch (Exception e) {
            System.err.println("Translation API error: " + e.getMessage());
        }

        return text; // Return original text in case of failure
    }
}

