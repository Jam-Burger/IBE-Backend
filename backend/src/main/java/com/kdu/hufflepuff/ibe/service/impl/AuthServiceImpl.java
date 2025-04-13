package com.kdu.hufflepuff.ibe.service.impl;

import com.kdu.hufflepuff.ibe.service.interfaces.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public boolean verifyAccessToken(String email, String accessToken) {
        log.info("Verifying access token for email: {}, Token: {}", email, accessToken);
        return true;
    }
}
