package com.ticket.app.exception.exceptions;

public class InvalidJwtUserIdException extends RuntimeException {
    public InvalidJwtUserIdException() {
        super("Invalid userId in JWT: subject is not a valid UUID.");
    }
}
