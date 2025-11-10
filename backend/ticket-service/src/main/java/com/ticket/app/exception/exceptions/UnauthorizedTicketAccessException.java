package com.ticket.app.exception.exceptions;

public class UnauthorizedTicketAccessException extends RuntimeException {
    public UnauthorizedTicketAccessException() {
        super("You are not allowed to validate this ticket.");
    }
}
