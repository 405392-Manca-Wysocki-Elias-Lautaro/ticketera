package com.ticket.app.exception.exceptions;

public class TicketEventMismatchException extends RuntimeException {
    public TicketEventMismatchException() {
        super("The ticket does not belong to the selected event.");
    }
}
