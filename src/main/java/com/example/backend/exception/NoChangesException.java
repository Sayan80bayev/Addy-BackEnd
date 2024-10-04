package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoChangesException extends RuntimeException {

    public NoChangesException(String message) {
        super(message);
    }

    // Constructor that accepts a message and a cause
    public NoChangesException(String message, Throwable cause) {
        super(message, cause);
    }

}
