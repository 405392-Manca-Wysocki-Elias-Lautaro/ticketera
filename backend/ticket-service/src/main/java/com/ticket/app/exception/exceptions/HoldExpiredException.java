package com.ticket.app.exception.exceptions;

/**
 * Thrown when trying to use or convert an expired hold.
 */
public class HoldExpiredException extends RuntimeException {
    public HoldExpiredException() {
        super("This hold has already expired.");
    }
}
