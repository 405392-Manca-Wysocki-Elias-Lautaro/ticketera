package com.ticket.app.exception.exceptions;

/**
 * Thrown when a ticket is expired or no longer valid.
 */
public class TicketExpiredException extends RuntimeException {
    public TicketExpiredException() {
        super("The ticket has expired.");
    }
}
