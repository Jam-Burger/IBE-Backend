package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.util.LoggingUtil;
import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/health")
public class HealthCheckController {

    @GetMapping
    public ApiResponse<Map<String, String>> healthCheck() {
        // Create structured log entry for the health check request
        Map<String, Object> logEntry = LoggingUtil.createLogEntry("HealthCheck");
        LoggingUtil.addField(logEntry, "component", "HealthCheckController");
        log.info("Health check request received: {}", logEntry);
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
            .statusCode(HttpStatus.OK)
            .message("Health check successful")
            .data(Map.of("status", "Server is Up and Running..."))
            .build();
        
        // Log the response using structured logging
        LoggingUtil.addField(logEntry, "statusCode", response.getStatusCode().value());
        LoggingUtil.addField(logEntry, "responseData", response.getData());
        LoggingUtil.addField(logEntry, "outcome", "success");
        log.info("Health check completed: {}", logEntry);
        
        return response;
    }
}
