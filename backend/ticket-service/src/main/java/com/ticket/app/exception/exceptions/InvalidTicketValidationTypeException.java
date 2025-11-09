package com.ticket.app.exception.exceptions;

/**
 * Thrown when a ticket validation request contains an unsupported type.
 * Valid types are "QR" and "CODE".
 */
public class InvalidTicketValidationTypeException extends RuntimeException {

    public InvalidTicketValidationTypeException() {
        super("Invalid validation type. Accepted values are 'QR' or 'CODE'.");
    }

    public InvalidTicketValidationTypeException(String type) {
        super("Invalid validation type: '" + type + "'. Accepted values are 'QR' or 'CODE'.");
    }
}
