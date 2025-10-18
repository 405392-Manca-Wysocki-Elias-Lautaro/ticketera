package com.auth.app.exception.exceptions;

public class UserAlreadyVerifiedException extends RuntimeException {

    public UserAlreadyVerifiedException() {
        super("User is already verified.");
    }
}

