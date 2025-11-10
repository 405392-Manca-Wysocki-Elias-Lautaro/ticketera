package com.ticket.app.exception.exceptions;

/**
 * Thrown when a ticket could not be found or has been deleted.
 */
public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException() {
        super("The ticket does not exist or has been deleted.");
    }
}
