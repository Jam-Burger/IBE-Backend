package com.kdu.hufflepuff.ibe.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Simplified utility for business event logging
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingUtil {
    /**
     * Create a business event log
     */
    public static EventBuilder event(String eventName) {
        return new EventBuilder(eventName);
    }

    /**
     * Fluent API for business events
     */
    public static class EventBuilder {
        private final Map<String, Object> data = new HashMap<>();

        private EventBuilder(String eventName) {
            data.put("event", eventName);

            // Include context from MDC
            addIfPresent("requestId");
            addIfPresent("path");

            // Add IP address for business events only
            getCurrentRequest().ifPresent(request -> {
                data.put("method", request.getMethod());
                data.put("ipAddress", request.getRemoteAddr());
            });
        }

        /**
         * Gets the current HTTP request if available
         */
        private static Optional<HttpServletRequest> getCurrentRequest() {
            try {
                return Optional.ofNullable(RequestContextHolder.getRequestAttributes()).filter(ServletRequestAttributes.class::isInstance).map(attributes -> ((ServletRequestAttributes) attributes).getRequest());
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        private void addIfPresent(String key) {
            String value = MDC.get(key);
            if (value != null) data.put(key, value);
        }

        public EventBuilder field(String key, Object value) {
            if (value != null) data.put(key, value);
            return this;
        }

        public void log(Logger logger) {
            logger.info("Business event: {}", data);
        }
    }
}