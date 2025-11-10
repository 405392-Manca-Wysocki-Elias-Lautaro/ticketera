package com.ticket.app.exception.exceptions;

/**
 * Thrown when the provided QR token is invalid or corrupted.
 */
public class InvalidQrTokenException extends RuntimeException {
    public InvalidQrTokenException() {
        super("The provided QR code is invalid or corrupted.");
    }
}
