package com.clinic.Exception;

public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException(String message) {
        super(message);
    }

    public BadCredentialsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
