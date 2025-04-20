package com.kdu.hufflepuff.ibe.config;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.EC2Plugin;
import com.amazonaws.xray.plugins.ECSPlugin;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;
import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class XRayConfig {

    static {
        try {
            AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard()
                    .withPlugin(new EC2Plugin())
                    .withPlugin(new ECSPlugin());

            // Use a local sampling rules file instead of centralized strategy
            builder.withSamplingStrategy(new LocalizedSamplingStrategy(new ClassPathResource("sampling-rules.json").getInputStream().markSupported()));

            AWSXRay.setGlobalRecorder(builder.build());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load X-Ray sampling rules", e);
        }
    }

    @Bean
    public Filter tracingFilter() {
        return new AWSXRayServletFilter("ibe-backend");
    }
} 