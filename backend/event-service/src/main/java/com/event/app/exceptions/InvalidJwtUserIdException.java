package com.event.app.exceptions;

public class InvalidJwtUserIdException extends RuntimeException {
    public InvalidJwtUserIdException() {
        super("Invalid user ID in JWT");
    }
}

