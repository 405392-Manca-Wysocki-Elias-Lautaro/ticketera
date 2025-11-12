package com.event.app.exceptions;

public class JwtClaimNotFoundException extends RuntimeException {
    public JwtClaimNotFoundException(String claimName) {
        super("JWT claim not found: " + claimName);
    }
}

