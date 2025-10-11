package com.notification.app.exceptions.custom;

public class UnsupportedNotificationTypeException extends RuntimeException {

    public UnsupportedNotificationTypeException() {
        super("The specified notification type is not supported for this channel.");
    }

    public UnsupportedNotificationTypeException(Throwable cause) {
        super("The specified notification type is not supported for this channel.", cause);
    }
}
