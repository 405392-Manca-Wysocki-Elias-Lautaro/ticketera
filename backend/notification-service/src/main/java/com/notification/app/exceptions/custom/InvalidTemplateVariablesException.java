package com.notification.app.exceptions.custom;

public class InvalidTemplateVariablesException extends RuntimeException {

    public InvalidTemplateVariablesException() {
        super("Invalid or missing variables for the notification template.");
    }

    public InvalidTemplateVariablesException(Throwable cause) {
        super("Invalid or missing variables for the notification template.", cause);
    }
}
