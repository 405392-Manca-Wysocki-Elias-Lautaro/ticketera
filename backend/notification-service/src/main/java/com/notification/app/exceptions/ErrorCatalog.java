package com.notification.app.exceptions;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public enum ErrorCatalog {

    // 🧱 Generic
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, ErrorCodes.BAD_REQUEST,
            "The request is invalid or malformed."),
    // 📄 Template
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCodes.TEMPLATE_NOT_FOUND,
            "Template not found."),
    TEMPLATE_RENDER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.TEMPLATE_RENDER_ERROR,
            "Error rendering the notification template."),
    // 📧 Email
    EMAIL_SEND_FAILED(HttpStatus.BAD_GATEWAY, ErrorCodes.EMAIL_SEND_FAILED,
            "Failed to send email through the configured provider."),
    INVALID_EMAIL_ADDRESS(HttpStatus.BAD_REQUEST, ErrorCodes.INVALID_EMAIL_ADDRESS,
            "Invalid email address provided."),
    // 📬 Notification
    NOTIFICATION_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.NOTIFICATION_PROCESSING_ERROR,
            "An error occurred while processing the notification event."),
    // 📢 Notification validation
    INVALID_NOTIFICATION_CHANNEL(HttpStatus.BAD_REQUEST, ErrorCodes.INVALID_NOTIFICATION_CHANNEL,
            "Invalid channel for the specified notification type."),
    UNSUPPORTED_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, ErrorCodes.UNSUPPORTED_NOTIFICATION_TYPE,
            "The specified notification type is not supported for this channel."),
    // 🧩 Template variables
    INVALID_TEMPLATE_VARIABLES(HttpStatus.BAD_REQUEST, ErrorCodes.INVALID_TEMPLATE_VARIABLES,
            "Invalid or missing variables for the notification template."),
    // 📡 Channel
    UNSUPPORTED_CHANNEL(HttpStatus.BAD_REQUEST, ErrorCodes.UNSUPPORTED_CHANNEL,
            "The specified notification channel is not supported.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCatalog(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
