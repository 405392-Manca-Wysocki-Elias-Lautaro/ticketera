package com.order.app.exceptions;

/**
 * Exception thrown when the user ID in JWT cannot be parsed as UUID.
 */
public class InvalidJwtUserIdException extends RuntimeException {
    public InvalidJwtUserIdException() {
        super("Invalid user ID format in JWT token");
    }
    
    public InvalidJwtUserIdException(String message) {
        super(message);
    }
}

