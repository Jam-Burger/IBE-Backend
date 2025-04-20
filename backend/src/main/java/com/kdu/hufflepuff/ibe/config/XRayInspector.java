package com.kdu.hufflepuff.ibe.config;

import com.amazonaws.xray.spring.aop.BaseAbstractXRayInterceptor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class XRayInspector extends BaseAbstractXRayInterceptor {
    @Override
    @Pointcut("@within(com.amazonaws.xray.spring.aop.XRayEnabled) && (bean(*Controller) || bean(*Service) || bean(*Repository))")
    public void xrayEnabledClasses() {
        // Pointcut definition
    }
} 