package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @GetMapping
    public ApiResponse<Map<String, String>> healthCheck() {
        return ApiResponse.<Map<String, String>>builder()
            .statusCode(HttpStatus.OK)
            .message("Health check successful")
            .data(Map.of("status", "Server is Up and Running..."))
            .build();
    }
}
