package com.ticket.app.exception;

public final class ErrorCodes {

    private ErrorCodes() {
    }

    // 🎟️ Ticket domain
    public static final String TICKET_NOT_FOUND = "TICKET_NOT_FOUND";
    public static final String TICKET_ALREADY_CHECKED_IN = "TICKET_ALREADY_CHECKED_IN";
    public static final String INVALID_TICKET_STATUS = "INVALID_TICKET_STATUS";
    public static final String INVALID_QR_TOKEN = "INVALID_QR_TOKEN";
    public static final String EXPIRED_TICKET = "EXPIRED_TICKET";
    public static final String INVALID_TICKET_VALIDATION_TYPE = "INVALID_TICKET_VALIDATION_TYPE";

    // 🧾 Holds
    public static final String HOLD_NOT_FOUND = "HOLD_NOT_FOUND";
    public static final String HOLD_EXPIRED = "HOLD_EXPIRED";
    public static final String HOLD_CONVERSION_ERROR = "HOLD_CONVERSION_ERROR";
    public static final String INVALID_HOLD_QUANTITY = "INVALID_HOLD_QUANTITY";
    public static final String SEAT_ALREADY_HELD = "SEAT_ALREADY_HELD";

    // ⚙️ Infrastructure
    public static final String DATABASE_ERROR = "DATABASE_ERROR";
    public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    public static final String INTEGRATION_ERROR = "INTEGRATION_ERROR";

    // 🌐 Generic
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String CONFLICT = "CONFLICT";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
}
