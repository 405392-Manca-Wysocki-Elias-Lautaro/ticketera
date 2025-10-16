package com.auth.app.exception.exceptions;

public class WeakPasswordException extends RuntimeException {

    public WeakPasswordException() {
        super("Password is too weak");
    }

    public WeakPasswordException(String message) {
        super(message);
    }
}
