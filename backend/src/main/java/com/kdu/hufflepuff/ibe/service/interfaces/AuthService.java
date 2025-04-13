package com.kdu.hufflepuff.ibe.service.interfaces;

public interface AuthService {
    boolean verifyAccessToken(String email, String accessToken);
}
