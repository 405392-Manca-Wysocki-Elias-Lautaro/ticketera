package com.ticket.app.exception.exceptions;

/**
 * Thrown when a hold could not be found.
 */
public class HoldNotFoundException extends RuntimeException {
    public HoldNotFoundException() {
        super("Hold not found or already converted/expired.");
    }
}
