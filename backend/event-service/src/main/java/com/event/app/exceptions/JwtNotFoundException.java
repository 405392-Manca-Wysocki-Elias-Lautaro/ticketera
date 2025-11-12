package com.event.app.exceptions;

public class JwtNotFoundException extends RuntimeException {
    public JwtNotFoundException() {
        super("JWT not found in security context");
    }
}

