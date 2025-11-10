package com.auth.app.exception.exceptions;

public class InvalidOrUnknownTokenException extends RuntimeException {

    public InvalidOrUnknownTokenException() {
        super("Invalid or unknown verification token.");
    }
}
