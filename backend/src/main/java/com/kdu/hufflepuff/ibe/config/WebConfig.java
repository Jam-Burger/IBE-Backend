package com.kdu.hufflepuff.ibe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${application.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${application.cors.allowed-methods}")
    private String[] allowedMethods;

    @Value("${application.cors.allowed-headers}")
    private String[] allowedHeaders;

    @Value("${application.cors.max-age}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .maxAge(maxAge);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}