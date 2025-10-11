package com.notification.app.exceptions.custom;

public class EmailSendException extends RuntimeException {

    public EmailSendException() {
        super("Failed to send email through the configured provider.");
    }

    public EmailSendException(Throwable cause) {
        super("Failed to send email through the configured provider.", cause);
    }
}
