package com.kdu.hufflepuff.ibe.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;

@Getter
@Builder
@Slf4j
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final HttpStatusCode statusCode;
    private final String message;
    private final T data;
    private final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    public ResponseEntity<ApiResponse<T>> send() {
        log.debug(message);
        return ResponseEntity.status(statusCode).body(this);
    }
}
