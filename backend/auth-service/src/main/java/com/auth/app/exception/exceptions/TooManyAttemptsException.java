package com.auth.app.exception.exceptions;

import lombok.Getter;

@Getter
public class TooManyAttemptsException extends RuntimeException {

    public TooManyAttemptsException() {
        super("Too many failed login attempts. Please try again later.");
    }
}
