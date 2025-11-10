package com.auth.app.exception.exceptions;

public class TokenAlreadyUsedException extends RuntimeException {

    public TokenAlreadyUsedException() {
        super("The verification token has already been used.");
    }
}
