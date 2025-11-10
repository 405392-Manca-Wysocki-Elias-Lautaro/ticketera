package com.ticket.app.exception.exceptions;

public class JwtNotFoundException extends RuntimeException {
    public JwtNotFoundException() {
        super("No JWT found in SecurityContext. Authentication is missing or invalid.");
    }
}
