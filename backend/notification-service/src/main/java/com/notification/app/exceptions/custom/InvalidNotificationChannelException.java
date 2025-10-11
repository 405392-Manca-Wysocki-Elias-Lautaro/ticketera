package com.notification.app.exceptions.custom;

public class InvalidNotificationChannelException extends RuntimeException {

    public InvalidNotificationChannelException() {
        super("Invalid channel for the specified notification type.");
    }

    public InvalidNotificationChannelException(Throwable cause) {
        super("Invalid channel for the specified notification type.", cause);
    }
}
