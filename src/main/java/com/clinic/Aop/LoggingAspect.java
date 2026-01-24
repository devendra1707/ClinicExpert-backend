package com.clinic.Aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Aspect for logging HTTP requests and responses
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    /**
     * Log before controller method execution
     */
    @Before("execution(* com.clinic.Controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            log.info("═══════════════════════════════════════════════════════════");
            log.info("📥 INCOMING REQUEST");
            log.info("URL: {} {}", request.getMethod(), request.getRequestURL());
            log.info("Client IP: {}", request.getRemoteAddr());
            log.info("Method: {}.{}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());

            // Log headers (excluding sensitive ones)
            log.info("Headers: Authorization={}, Content-Type={}",
                    request.getHeader("Authorization") != null ? "Bearer ***" : "None",
                    request.getHeader("Content-Type"));

            log.info("═══════════════════════════════════════════════════════════");
        }
    }

    /**
     * Log after successful controller method execution
     */
    @AfterReturning(pointcut = "execution(* com.clinic.Controller..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("═══════════════════════════════════════════════════════════");
        log.info("📤 OUTGOING RESPONSE");
        log.info("Method: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());

        if (result != null) {
            log.info("Response Type: {}", result.getClass().getSimpleName());
        }

        log.info("Status: SUCCESS");
        log.info("═══════════════════════════════════════════════════════════");
    }

    /**
     * Log when controller method throws an exception
     */
    @AfterThrowing(pointcut = "execution(* com.clinic.Controller..*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("═══════════════════════════════════════════════════════════");
        log.error("❌ EXCEPTION THROWN");
        log.error("Method: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        log.error("Exception Type: {}", exception.getClass().getSimpleName());
        log.error("Exception Message: {}", exception.getMessage());
        log.error("═══════════════════════════════════════════════════════════");
    }
}
