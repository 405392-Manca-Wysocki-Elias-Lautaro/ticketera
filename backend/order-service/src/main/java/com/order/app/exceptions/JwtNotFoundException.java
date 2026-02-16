package com.order.app.exceptions;

/**
 * Exception thrown when JWT token is not found in the SecurityContext.
 */
public class JwtNotFoundException extends RuntimeException {
    public JwtNotFoundException() {
        super("JWT token not found in security context");
    }
    
    public JwtNotFoundException(String message) {
        super(message);
    }
}

