package com.auth.app.exception.exceptions;

public class CommonPasswordException extends RuntimeException {
    public CommonPasswordException() {
        super("The chosen password is too common or insecure");
    }
}
