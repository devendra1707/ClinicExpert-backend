package com.clinic.Exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Exceptions> handleUserNotFound(UsernameNotFoundException ex) {
        Exceptions error = new Exceptions(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                HttpStatusCode.valueOf(404)
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Exceptions> handleBadCredentials(BadCredentialsException ex) {
        Exceptions error = new Exceptions(
                "Incorrect password. Please try again or reset your password.",
                HttpStatus.UNAUTHORIZED,
                HttpStatusCode.valueOf(401)
        );
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Exceptions> handleDisabledException(DisabledException ex) {
        Exceptions error = new Exceptions(
                "Your account is currently disabled. Please contact support.",
                HttpStatus.FORBIDDEN,
                HttpStatusCode.valueOf(403)
        );
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Exceptions> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Exceptions error = new Exceptions(
                "A clinic with this email or contact number already exists.",
                HttpStatus.CONFLICT,
                HttpStatusCode.valueOf(409)
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Exceptions> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        Exceptions error = new Exceptions(
                errorMessage != null ? errorMessage : "Invalid input data provided.",
                HttpStatus.BAD_REQUEST,
                HttpStatusCode.valueOf(400)
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Exceptions> handleTransactionSystemException(TransactionSystemException ex) {
        String errorMessage = "Transaction failed at database level.";
        if (ex.getRootCause() instanceof ConstraintViolationException constraintEx) {
            errorMessage = constraintEx.getConstraintViolations().iterator().next().getMessage();
        } else {
            errorMessage = ex.getRootCause().getMessage();
        }

        Exceptions error = new Exceptions(
                errorMessage,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatusCode.valueOf(500)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Exceptions> handleGlobalException(Exception ex) {
        Exceptions error = new Exceptions(
                "Something went wrong on our end. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatusCode.valueOf(500)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
