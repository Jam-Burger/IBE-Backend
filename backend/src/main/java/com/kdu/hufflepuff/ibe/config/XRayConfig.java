package com.kdu.hufflepuff.ibe.config;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.EC2Plugin;
import com.amazonaws.xray.plugins.ECSPlugin;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@Import(XRayInspector.class)
@EnableAspectJAutoProxy
public class XRayConfig {
    static {
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard()
            .withDefaultPlugins()
            .withPlugin(new EC2Plugin())
            .withPlugin(new ECSPlugin());

        AWSXRay.setGlobalRecorder(builder.build());
    }

    @Value("${spring.application.name}")
    private String serviceName;

    @Bean
    public Filter tracingFilter() {
        return new AWSXRayServletFilter(request -> serviceName);
    }
} 