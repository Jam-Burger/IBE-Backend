package com.kdu.hufflepuff.ibe.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;

@Slf4j
@Getter
@Builder
@ToString
@RequiredArgsConstructor
public class ErrorResponse {
    private final HttpStatus statusCode;
    private final String message;
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public ResponseEntity<ErrorResponse> send() {
        log.error("API error: {}", this);
        return ResponseEntity.status(statusCode).body(this);
    }
}