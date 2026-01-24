package com.clinic.Exception;

import org.springframework.validation.Errors;

public class MethodArgumentNotValidException extends RuntimeException {

    public MethodArgumentNotValidException(String message) {
        super(message);
    }

    public MethodArgumentNotValidException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public Errors getBindingResult() {
        return null;
    }
}
