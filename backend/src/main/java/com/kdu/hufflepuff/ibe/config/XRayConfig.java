package com.kdu.hufflepuff.ibe.config;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.entities.Subsegment;
import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.EC2Plugin;
import com.amazonaws.xray.plugins.ECSPlugin;
import jakarta.servlet.Filter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

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

    @Aspect
    @Component
    public static class JpaTracingAspect {
        @Around("execution(* org.springframework.data.jpa.repository.JpaRepository+.*(..))")
        public Object traceJpaOperation(ProceedingJoinPoint joinPoint) throws Throwable {
            String operation = joinPoint.getSignature().getName();
            String repository = joinPoint.getTarget().getClass().getSimpleName();

            // Create a segment if there isn't one
            Segment segment = null;
            if (AWSXRay.getCurrentSegment() == null) {
                segment = AWSXRay.beginSegment("JPA-Operations");
            }

            try {
                Subsegment subsegment = AWSXRay.beginSubsegment("JPA-" + repository + "-" + operation);
                Object result = joinPoint.proceed();
                subsegment.end();
                return result;
            } catch (Throwable t) {
                Subsegment subsegment = AWSXRay.getCurrentSubsegment();
                if (subsegment != null) {
                    subsegment.addException(t);
                    subsegment.end();
                }
                throw t;
            } finally {
                if (segment != null) {
                    segment.end();
                }
            }
        }
    }
} 