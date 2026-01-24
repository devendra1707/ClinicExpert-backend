package com.clinic.Exception;

public class UsernameNotFoundException extends RuntimeException{

    public UsernameNotFoundException(String message) {
        super(message);
    }

    public UsernameNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
