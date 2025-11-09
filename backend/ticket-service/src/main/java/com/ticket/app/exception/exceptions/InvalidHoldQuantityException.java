package com.ticket.app.exception.exceptions;

/**
 * Thrown when an invalid quantity is specified for a general admission hold.
 */
public class InvalidHoldQuantityException extends RuntimeException {
    public InvalidHoldQuantityException() {
        super("Quantity must be greater than zero for general admission holds.");
    }
}
