package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Custom exception for forbidden access
@ResponseStatus(HttpStatus.FORBIDDEN) // Maps this exception to HTTP 403
public class ForbiddenException extends RuntimeException {

    // Constructor that accepts a message
    public ForbiddenException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
