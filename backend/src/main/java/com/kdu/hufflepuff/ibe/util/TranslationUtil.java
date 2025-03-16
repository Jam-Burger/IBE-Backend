package com.kdu.hufflepuff.ibe.util;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TranslationUtil {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String TRANSLATION_API_URL = "http://localhost:5000/translate";

    public <T> T translateObject(T object, String targetLanguage) {

        if ("en".equalsIgnoreCase(targetLanguage) || object == null) {
            return object; // No need to translate
        }

        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                // If field is a String, translate it
                if (field.getType().equals(String.class)) {
                    String originalText = (String) field.get(object);
                    if (originalText != null && !originalText.isEmpty()) {
                        String translatedText = translateText(originalText, targetLanguage);
                        field.set(object, translatedText);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error translating object", e);
        }

        return object;
    }

    public <T> List<T> translateList(List<T> list, String targetLanguage) {
        list.forEach(item -> translateObject(item, targetLanguage));
        return list;
    }

    private String translateText(String text, String targetLanguage) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("q", text);
        requestBody.put("source", "en");
        requestBody.put("target", targetLanguage);

        ResponseEntity<Map> response = restTemplate.postForEntity(TRANSLATION_API_URL, requestBody, Map.class);
        return response.getBody() != null ? (String) response.getBody().get("translatedText") : text;
    }
}
