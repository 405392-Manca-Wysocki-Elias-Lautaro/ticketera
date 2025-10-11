package com.notification.app.exceptions.custom;

public class UnsupportedChannelException extends RuntimeException {

    public UnsupportedChannelException() {
        super("The specified notification channel is not supported.");
    }

    public UnsupportedChannelException(Throwable cause) {
        super("The specified notification channel is not supported.", cause);
    }
}
