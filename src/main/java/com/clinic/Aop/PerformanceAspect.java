package com.clinic.Aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 * Aspect for monitoring API performance
 * Logs execution time and method details
 */
@Slf4j
@Aspect
@Component
public class PerformanceAspect {

    /**
     * Monitor all controller methods
     */
    @Around("execution(* com.clinic.Controller..*(..))")
    public Object monitorControllerPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorPerformance(joinPoint, "CONTROLLER");
    }

    /**
     * Monitor all service methods
     */
    @Around("execution(* com.clinic.Service..*(..))")
    public Object monitorServicePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorPerformance(joinPoint, "SERVICE");
    }

    /**
     * Monitor methods annotated with @PerformanceMonitor
     */
    @Around("@annotation(com.clinic.Annotation.PerformanceMonitor)")
    public Object monitorAnnotatedMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        return monitorPerformance(joinPoint, "CUSTOM");
    }

    /**
     * Core performance monitoring logic
     */
    private Object monitorPerformance(ProceedingJoinPoint joinPoint, String type) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        // Get method parameters
        Object[] args = joinPoint.getArgs();
        String parameters = args.length > 0 ? Arrays.toString(args) : "No parameters";

        // Start timing
        Instant start = Instant.now();

        log.info("┌─────────────────────────────────────────────────────────");
        log.info("│ [{}] STARTED: {}.{}", type, className, methodName);
        log.info("│ Parameters: {}", sanitizeParameters(parameters));

        Object result = null;
        boolean hasError = false;
        String errorMessage = null;

        try {
            // Execute the actual method
            result = joinPoint.proceed();
            return result;

        } catch (Exception e) {
            hasError = true;
            errorMessage = e.getClass().getSimpleName() + ": " + e.getMessage();
            throw e;

        } finally {
            // Calculate execution time
            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            long executionTime = duration.toMillis();

            // Log completion
            if (hasError) {
                log.error("│ [{}] FAILED: {}.{}", type, className, methodName);
                log.error("│ Error: {}", errorMessage);
                log.error("│ Execution Time: {} ms", executionTime);
                log.error("└─────────────────────────────────────────────────────────");
            } else {
                // Performance warning if method is slow
                if (executionTime > 1000) {
                    log.warn("│ [{}] COMPLETED (SLOW): {}.{}", type, className, methodName);
                    log.warn("│ ⚠️  Execution Time: {} ms (> 1 second)", executionTime);
                } else if (executionTime > 500) {
                    log.warn("│ [{}] COMPLETED: {}.{}", type, className, methodName);
                    log.warn("│ ⚠️  Execution Time: {} ms", executionTime);
                } else {
                    log.info("│ [{}] COMPLETED: {}.{}", type, className, methodName);
                    log.info("│ ✓ Execution Time: {} ms", executionTime);
                }

                // Log return type
                if (result != null) {
                    log.info("│ Return Type: {}", result.getClass().getSimpleName());
                }

                log.info("└─────────────────────────────────────────────────────────");
            }
        }
    }

    /**
     * Sanitize parameters to avoid logging sensitive data
     */
    private String sanitizeParameters(String params) {
        if (params.toLowerCase().contains("password")) {
            return params.replaceAll("password[^,]*", "password=***");
        }
        return params;
    }
}
