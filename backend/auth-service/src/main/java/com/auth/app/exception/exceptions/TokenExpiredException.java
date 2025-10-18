package com.auth.app.exception.exceptions;

/**
 * Se lanza cuando el token está vencido.
 */
public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() {
        super("Access token has expired");
    }
}

