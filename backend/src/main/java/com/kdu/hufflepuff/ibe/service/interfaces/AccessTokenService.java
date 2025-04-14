package com.kdu.hufflepuff.ibe.service.interfaces;

public interface AccessTokenService {
    boolean verifyAccessToken(String email, String accessToken);
}
