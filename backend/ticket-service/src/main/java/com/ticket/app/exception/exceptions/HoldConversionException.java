package com.ticket.app.exception.exceptions;

/**
 * Thrown when a hold cannot be converted (for example, already converted).
 */
public class HoldConversionException extends RuntimeException {
    public HoldConversionException() {
        super("The hold cannot be converted because it is not active.");
    }
}
