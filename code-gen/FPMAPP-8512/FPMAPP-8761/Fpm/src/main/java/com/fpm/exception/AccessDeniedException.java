package com.fpm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {

    // STORY: FPMAPP-8761 - Custom exception for access denial to currency override interface
    public AccessDeniedException(String message) {
        super(message);
    }
}