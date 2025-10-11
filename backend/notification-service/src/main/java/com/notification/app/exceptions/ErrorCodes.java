package com.notification.app.exceptions;

public final class ErrorCodes {

    private ErrorCodes() {
    }

    // 🔧 Generic
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    public static final String BAD_REQUEST = "BAD_REQUEST";

    // 📄 Template & Rendering
    public static final String TEMPLATE_NOT_FOUND = "TEMPLATE_NOT_FOUND";
    public static final String TEMPLATE_RENDER_ERROR = "TEMPLATE_RENDER_ERROR";

    // 📧 Email & Delivery
    public static final String EMAIL_SEND_FAILED = "EMAIL_SEND_FAILED";
    public static final String INVALID_EMAIL_ADDRESS = "INVALID_EMAIL_ADDRESS";

    // 📬 Notification Processing
    public static final String NOTIFICATION_PROCESSING_ERROR = "NOTIFICATION_PROCESSING_ERROR";

    // 📡 Notification Channel
    public static final String UNSUPPORTED_CHANNEL = "UNSUPPORTED_CHANNEL";

    // 📢 Notification Validation
    public static final String INVALID_NOTIFICATION_CHANNEL = "INVALID_NOTIFICATION_CHANNEL";
    public static final String UNSUPPORTED_NOTIFICATION_TYPE = "UNSUPPORTED_NOTIFICATION_TYPE";

    // 🧩 Template Variables
    public static final String INVALID_TEMPLATE_VARIABLES = "INVALID_TEMPLATE_VARIABLES";

}
