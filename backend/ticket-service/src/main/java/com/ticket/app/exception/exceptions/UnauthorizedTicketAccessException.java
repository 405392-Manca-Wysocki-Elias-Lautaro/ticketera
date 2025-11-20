package com.ticket.app.exception.exceptions;

public class UnauthorizedTicketAccessException extends RuntimeException {
    public UnauthorizedTicketAccessException() {
        super("You are not authorized to perform this action");
    }
}
