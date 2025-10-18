package com.auth.app.exception.exceptions;

/**
 * Se lanza cuando el usuario intenta loguearse con email o contraseña incorrectos.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}

