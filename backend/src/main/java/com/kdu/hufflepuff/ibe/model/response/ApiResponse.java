package com.kdu.hufflepuff.ibe.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;

@Slf4j
@Getter
@Builder
@ToString
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final HttpStatusCode statusCode;
    private final String message;
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    @ToString.Exclude
    private final T data;

    public ResponseEntity<ApiResponse<T>> send() {
        log.info("API response: {}", this);
        return ResponseEntity.status(statusCode).body(this);
    }
}
