package com.ticket.app.exception.exceptions;

public class JwtClaimNotFoundException extends RuntimeException {
    public JwtClaimNotFoundException(String claimName) {
        super("JWT claim '" + claimName + "' is missing or empty.");
    }

    public JwtClaimNotFoundException(String claimName, String message) {
        super("JWT claim '" + claimName + "' is invalid: " + message);
    }
}
