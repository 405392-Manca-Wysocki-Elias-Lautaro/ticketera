package com.ticket.app.exception.exceptions;

/**
 * Thrown when attempting to hold a seat that is already held or reserved.
 */
public class SeatAlreadyHeldException extends RuntimeException {
    public SeatAlreadyHeldException() {
        super("The selected seat is already on hold or reserved.");
    }

}
