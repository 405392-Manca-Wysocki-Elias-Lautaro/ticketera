package com.auth.app.exception.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User id " + id + " not found");
    }
}

