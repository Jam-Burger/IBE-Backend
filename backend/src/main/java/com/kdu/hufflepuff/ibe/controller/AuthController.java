package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.dto.out.UserWithTokenResponseDTO;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.security.AuthUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/{tenantId}/auth")
public class AuthController {
    private final AuthUserService authUserService;

    @PostMapping("/login")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserWithTokenResponseDTO>> login(Authentication authentication) {
        UserWithTokenResponseDTO registeredUser = authUserService.login(authentication);
        return ApiResponse.<UserWithTokenResponseDTO>builder()
            .statusCode(HttpStatus.OK)
            .message("User logged in")
            .data(registeredUser)
            .build()
            .send();
    }
}
