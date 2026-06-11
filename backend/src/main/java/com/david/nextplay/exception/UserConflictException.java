package com.david.nextplay.exception;

public class UserConflictException extends RuntimeException {
    
    public UserConflictException(String message) {
        super(message);
    }
}
