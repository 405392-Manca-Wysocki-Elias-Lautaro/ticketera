package com.ticket.app.exception.exceptions;

/**
 * Thrown when attempting to check in a ticket that has already been validated.
 */
public class TicketAlreadyCheckedInException extends RuntimeException {
    public TicketAlreadyCheckedInException() {
        super("The ticket has already been checked in.");
    }
}
