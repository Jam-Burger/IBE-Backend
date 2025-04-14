package com.kdu.hufflepuff.ibe.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdu.hufflepuff.ibe.service.interfaces.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public boolean verifyAccessToken(String email, String accessToken) {
        try {
            String[] parts = accessToken.split("\\.");
            if (parts.length < 2) {
                log.warn("Invalid JWT token.");
                return false;
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(
                payload, new TypeReference<>() {
                });
            String tokenEmail = (String) claims.get("email");
            return email.equalsIgnoreCase(tokenEmail);
        } catch (Exception e) {
            log.error("Failed to decode JWT token.", e);
            return false;
        }
    }
}
