package com.ticket.app.exception.exceptions;

/**
 * Thrown when a ticket is in an invalid status for the requested operation.
 */
public class InvalidTicketStatusException extends RuntimeException {
    public InvalidTicketStatusException() {
        super("The ticket is not in a valid status for this operation.");
    }
}
