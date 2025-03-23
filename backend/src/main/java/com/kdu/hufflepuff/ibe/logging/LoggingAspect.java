package com.kdu.hufflepuff.ibe.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class LoggingAspect {
    @Pointcut("within(com.kdu.hufflepuff.ibe.controller..*) || " +
        "within(com.kdu.hufflepuff.ibe.service..*) || " +
        "within(com.kdu.hufflepuff.ibe.repository..*)")
    public void applicationPointcut() {
    }

    @Around("applicationPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("Enter: {}.{}", className, methodName);
        long startTime = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            long executionTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            log.debug("Exit: {}.{} ({}ms)", className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            String context = String.format("Exception in %s.%s", className, methodName);
            log.error("{}: {}", context, e.getMessage(), e);

            if (e instanceof RuntimeException) {
                throw new ContextualRuntimeException(context, e);
            } else {
                throw e;
            }
        }
    }

    /**
     * Custom runtime exception that adds execution context information
     */
    public static class ContextualRuntimeException extends NestedRuntimeException {
        public ContextualRuntimeException(String msg, Throwable cause) {
            super(msg + ": " + cause.getMessage(), cause);
        }
    }
}