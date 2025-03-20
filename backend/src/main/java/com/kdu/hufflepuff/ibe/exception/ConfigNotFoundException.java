package com.kdu.hufflepuff.ibe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConfigNotFoundException extends RuntimeException {
    public ConfigNotFoundException(String tenantId, String configType) {
        super(String.format("Configuration not found for tenant '%s' and type '%s'", tenantId, configType));
    }
} 