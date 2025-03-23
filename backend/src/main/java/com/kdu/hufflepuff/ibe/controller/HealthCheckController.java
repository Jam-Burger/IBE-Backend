package com.kdu.hufflepuff.ibe.controller;

import com.kdu.hufflepuff.ibe.model.response.ApiResponse;
import com.kdu.hufflepuff.ibe.util.LoggingUtil;
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
        LoggingUtil.EventBuilder eventBuilder = LoggingUtil.event("HealthCheck");
        eventBuilder.field("component", "HealthCheckController");

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
            .statusCode(HttpStatus.OK)
            .message("Health check successful")
            .data(Map.of("status", "Server is Up and Running..."))
            .build();

        eventBuilder.field("status", "Server is Up and Running...").log(log);

        return response;
    }
}
