package com.ticket.app.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum ErrorCatalog {

    // 🎟️ Tickets
    TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCodes.TICKET_NOT_FOUND,
            "The ticket does not exist or has been deleted."),
    TICKET_ALREADY_CHECKED_IN(HttpStatus.CONFLICT, ErrorCodes.TICKET_ALREADY_CHECKED_IN,
            "The ticket has already been validated."),
    INVALID_TICKET_STATUS(HttpStatus.BAD_REQUEST, ErrorCodes.INVALID_TICKET_STATUS,
            "The ticket is not in a valid status for this operation."),
    INVALID_QR_TOKEN(HttpStatus.BAD_REQUEST, ErrorCodes.INVALID_QR_TOKEN,
            "The provided QR code is invalid or corrupted."),
    EXPIRED_TICKET(HttpStatus.GONE, ErrorCodes.EXPIRED_TICKET,
            "The ticket has expired."),
    INVALID_TICKET_VALIDATION_TYPE(HttpStatus.BAD_REQUEST, ErrorCodes.INVALID_TICKET_VALIDATION_TYPE,
            "Invalid ticket validation type. Accepted values are 'QR' or 'CODE'."),
    // 🧾 Holds
    HOLD_CONVERSION_ERROR(HttpStatus.CONFLICT, ErrorCodes.HOLD_CONVERSION_ERROR,
            "The hold cannot be converted because it is not active."),
    INVALID_HOLD_QUANTITY(HttpStatus.BAD_REQUEST, ErrorCodes.INVALID_HOLD_QUANTITY,
            "Quantity must be greater than zero for general admission holds."),
    SEAT_ALREADY_HELD(HttpStatus.CONFLICT, ErrorCodes.SEAT_ALREADY_HELD,
            "The selected seat is already on hold or reserved."),
    HOLD_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCodes.HOLD_NOT_FOUND,
            "The hold does not exist or has been deleted."),
    HOLD_EXPIRED(HttpStatus.GONE, ErrorCodes.HOLD_EXPIRED,
            "The hold has expired."),
    // ⚙️ Infrastructure
    DATABASE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodes.DATABASE_ERROR,
            "Database access error."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodes.SERVICE_UNAVAILABLE,
            "The ticket service is temporarily unavailable."),
    INTEGRATION_ERROR(HttpStatus.BAD_GATEWAY, ErrorCodes.INTEGRATION_ERROR,
            "Error while communicating with an external service."),
    // 🌐 Generic
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION_FAILED,
            "The request contains invalid data."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, ErrorCodes.BAD_REQUEST,
            "Invalid request."),
    CONFLICT(HttpStatus.CONFLICT, ErrorCodes.CONFLICT,
            "Conflict with the submitted data."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_SERVER_ERROR,
            "Internal server error in the ticket service.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCatalog(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
