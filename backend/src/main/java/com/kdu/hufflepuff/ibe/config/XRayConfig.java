package com.kdu.hufflepuff.ibe.config;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class XRayConfig {
    static {
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard().withDefaultPlugins();
        AWSXRay.setGlobalRecorder(builder.build());
    }

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean
    public Filter tracingFilter() {
        return new AWSXRayServletFilter(serviceName);
    }
} 