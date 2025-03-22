package com.kdu.hufflepuff.ibe.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Builder
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final HttpStatusCode statusCode;
    private final String message;
    private final T data;
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public ResponseEntity<ApiResponse<T>> send() {
        // Create structured log with key-value pairs
        Map<String, Object> logContext = new HashMap<>();
        logContext.put("statusCode", statusCode.value());
        logContext.put("message", message);
        logContext.put("timestamp", timestamp);
        logContext.put("responseType", "ApiResponse");
        
        // If data is not null and not too large, include it in logs
        if (data != null && data.toString().length() < 1000) {
            logContext.put("data", data);
        }
        log.info("API response: {}", logContext);
        return ResponseEntity.status(statusCode).body(this);
    }
}
