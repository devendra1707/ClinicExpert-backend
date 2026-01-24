package com.clinic.Exception;

public class TransactionSystemException extends RuntimeException {

    public TransactionSystemException(String message) {
        super(message);
    }

    public TransactionSystemException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public Throwable getRootCause() {
        Throwable cause = this;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

}
