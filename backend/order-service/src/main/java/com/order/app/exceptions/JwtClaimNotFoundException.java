package com.order.app.exceptions;

/**
 * Exception thrown when a required claim is not found in the JWT.
 */
public class JwtClaimNotFoundException extends RuntimeException {
    public JwtClaimNotFoundException(String claimName) {
        super("Required JWT claim not found: " + claimName);
    }
}

