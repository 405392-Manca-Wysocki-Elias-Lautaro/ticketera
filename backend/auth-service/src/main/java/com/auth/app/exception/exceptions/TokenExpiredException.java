package com.auth.app.exception.exceptions;

/**
 * Se lanza cuando el token de acceso (JWT) está vencido.
 */
public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException() {
        super("Access token has expired");
    }

    public TokenExpiredException(String message) {
        super(message);
    }
}

