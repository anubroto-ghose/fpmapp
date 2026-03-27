package com.fpm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {

    // STORY: FPMAPP-8760 - Custom exception for access denied with user-friendly message
    public AccessDeniedException() {
        super("Access denied: You do not have sufficient permissions to access this interface.");
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
