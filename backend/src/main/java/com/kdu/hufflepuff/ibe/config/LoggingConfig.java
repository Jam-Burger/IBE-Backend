package com.kdu.hufflepuff.ibe.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Configuration  
@ComponentScan(basePackages = "com.kdu.hufflepuff.ibe.logging")
public class LoggingConfig {
    
    /**
     * Adds basic request context to logs via MDC
     */
    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> loggingMdcFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registrationBean = new FilterRegistrationBean<>();
        
        OncePerRequestFilter filter = new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                try {
                    // Add unique request ID
                    MDC.put("requestId", UUID.randomUUID().toString());
                    // Add request path
                    MDC.put("path", request.getRequestURI());
                    
                    filterChain.doFilter(request, response);
                } finally {
                    MDC.clear();
                }
            }
        };
        
        registrationBean.setFilter(filter);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}