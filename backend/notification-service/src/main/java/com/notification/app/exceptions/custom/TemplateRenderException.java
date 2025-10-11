package com.notification.app.exceptions.custom;

public class TemplateRenderException extends RuntimeException {

    public TemplateRenderException() {
        super("Error rendering the notification template.");
    }

    public TemplateRenderException(Throwable cause) {
        super("Error rendering the notification template.", cause);
    }
}
