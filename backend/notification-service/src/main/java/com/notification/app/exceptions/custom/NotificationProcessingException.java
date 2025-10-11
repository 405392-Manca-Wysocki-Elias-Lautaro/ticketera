package com.notification.app.exceptions.custom;

public class NotificationProcessingException extends RuntimeException {

    public NotificationProcessingException() {
        super("An error occurred while processing the notification event.");
    }

    public NotificationProcessingException(Throwable cause) {
        super("An error occurred while processing the notification event.", cause);
    }
}
