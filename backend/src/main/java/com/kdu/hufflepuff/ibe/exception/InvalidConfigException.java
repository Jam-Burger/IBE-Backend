package com.kdu.hufflepuff.ibe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidConfigException extends RuntimeException {
    public InvalidConfigException(String message) {
        super(message);
    }

    public static InvalidConfigException nullTenantId() {
        return new InvalidConfigException("TenantId cannot be null or empty");
    }

    public static InvalidConfigException nullConfigType() {
        return new InvalidConfigException("ConfigType cannot be null or empty");
    }

    public static InvalidConfigException nullConfigData() {
        return new InvalidConfigException("Config data cannot be null");
    }

    public static InvalidConfigException unsupportedConfigType(String configType) {
        return new InvalidConfigException("Unsupported config type: " + configType);
    }
} 