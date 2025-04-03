package com.kdu.hufflepuff.ibe.exception;

public class ConfigUpdateException extends RuntimeException {
    public ConfigUpdateException(String message) {
        super(message);
    }

    public ConfigUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
} 