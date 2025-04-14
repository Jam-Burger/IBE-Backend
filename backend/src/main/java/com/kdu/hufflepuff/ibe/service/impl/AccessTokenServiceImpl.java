package com.kdu.hufflepuff.ibe.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdu.hufflepuff.ibe.service.interfaces.AccessTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccessTokenServiceImpl implements AccessTokenService {
    @Override
    public boolean verifyAccessToken(String email, String accessToken) {
        try {
            String[] parts = accessToken.split("\\.");
            if (parts.length < 2) {
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
            return false;
        }
    }
}
