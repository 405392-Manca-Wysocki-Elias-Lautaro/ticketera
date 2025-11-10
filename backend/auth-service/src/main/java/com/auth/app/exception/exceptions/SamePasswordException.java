package com.auth.app.exception.exceptions;

public class SamePasswordException extends RuntimeException {
    public SamePasswordException() {
        super("New password cannot be the same as the previous one");
    }
}
