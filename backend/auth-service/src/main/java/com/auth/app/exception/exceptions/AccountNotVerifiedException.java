package com.auth.app.exception.exceptions;

public class AccountNotVerifiedException extends RuntimeException {
    public AccountNotVerifiedException() {
        super("Please verify your email before logging in");
    }
}
