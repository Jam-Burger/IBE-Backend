package com.kdu.hufflepuff.ibe.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
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

        // Log method entry
        log.debug("Method entry: {}.{}", className, methodName);

        // Measure execution time
        long startTime = System.nanoTime();

        try {
            // Execute the method
            Object result = joinPoint.proceed();
            long executionTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

            // Log method exit with execution time
            log.debug("Method exit: {}.{} ({}ms)", className, methodName, executionTime);

            return result;
        } catch (Exception e) {
            // Log exception with context
            String context = String.format("Exception occurred in %s.%s", className, methodName);
            log.error("{}: {}", context, e.getMessage(), e);

            // Add method context to exceptions for better error reporting
            if (e instanceof RuntimeException) {
                // For runtime exceptions, we can wrap them safely
                throw new ContextualRuntimeException(context, e);
            } else {
                // For checked exceptions, we need to preserve the original type
                // Log with context but rethrow original to maintain compatibility
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

        @Override
        public String getMessage() {
            return NestedExceptionUtils.buildMessage(super.getMessage(), getCause());
        }
    }
} 