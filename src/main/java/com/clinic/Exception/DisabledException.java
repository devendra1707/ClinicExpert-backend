package com.clinic.Exception;

public class DisabledException extends RuntimeException {

    public DisabledException(String message) {
        super(message);
    }

    public DisabledException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
