package com.clinic.Aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for centralized exception handling and logging
 */
@Slf4j
@Aspect
@Component
public class ExceptionHandlingAspect {

    @AfterThrowing(
            pointcut = "execution(* com.clinic.Service..*(..))",
            throwing = "exception"
    )
    public void logServiceException(Exception exception) {
        log.error("🔴 Exception in Service Layer: {}", exception.getMessage());
        log.error("Stack Trace: ", exception);
    }

    @AfterThrowing(
            pointcut = "execution(* com.clinic.Repository..*(..))",
            throwing = "exception"
    )
    public void logRepositoryException(Exception exception) {
        log.error("🔴 Exception in Repository Layer: {}", exception.getMessage());
        log.error("Stack Trace: ", exception);
    }
}