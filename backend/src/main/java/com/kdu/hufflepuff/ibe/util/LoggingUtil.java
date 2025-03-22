package com.kdu.hufflepuff.ibe.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Utility class to standardize structured logging across the application
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingUtil {
    /**
     * Creates a structured log entry with standard fields
     *
     * @param eventName Name of the event (e.g., "UserLogin", "PaymentProcessed")
     * @return Map with base fields
     */
    public static Map<String, Object> createLogEntry(String eventName) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("event", eventName);
        
        // Add current HTTP request details if available
        getCurrentHttpRequest().ifPresent(request -> {
            logEntry.put("path", request.getRequestURI());
            logEntry.put("method", request.getMethod());
            logEntry.put("clientIp", request.getRemoteAddr());
        });
        
        // Add requestId from MDC if present
        if (MDC.get("requestId") != null) {
            logEntry.put("requestId", MDC.get("requestId"));
        }
        
        return logEntry;
    }
    
    /**
     * Adds key-value data to a log entry
     *
     * @param logEntry The existing log entry
     * @param key      The key for the data
     * @param value    The value to add
     */
    public static void addField(Map<String, Object> logEntry, String key, Object value) {
        if (value != null) {
            logEntry.put(key, value);
        }
    }
    
    /**
     * Logs a business event with standard formatting
     *
     * @param logger The SLF4J logger to use
     * @param eventName The name of the event
     * @param message Human-readable message
     * @param data Additional data to include
     */
    public static void logBusinessEvent(Logger logger, String eventName, String message, Map<String, Object> data) {
        Map<String, Object> logEntry = createLogEntry(eventName);
        logEntry.put("message", message);
        
        if (data != null) {
            logEntry.putAll(data);
        }
        
        logger.info("Business event: {}", logEntry);
    }
    
    /**
     * Gets the current HTTP request if available
     *
     * @return Optional containing the request, or empty if not in a request context
     */
    private static Optional<HttpServletRequest> getCurrentHttpRequest() {
        try {
            return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                    .filter(ServletRequestAttributes.class::isInstance)
                    .map(attributes -> ((ServletRequestAttributes) attributes).getRequest());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
} 