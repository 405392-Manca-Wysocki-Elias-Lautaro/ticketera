package com.auth.app.exception;

public final class ErrorCodes {

    private ErrorCodes() {} // evitar instanciación

    // 🔐 Auth / Security
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String INVALID_TOKEN = "INVALID_TOKEN";
    public static final String TOO_MANY_ATTEMPTS = "TOO_MANY_ATTEMPTS";
    
    // 📥 Validaciones
    public static final String VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String MISSING_FIELDS = "MISSING_FIELDS";
    public static final String INVALID_FORMAT = "INVALID_FORMAT";
    public static final String WEAK_PASSWORD = "WEAK_PASSWORD";
    public static final String COMMON_PASSWORD = "COMMON_PASSWORD";
    
    // 🧑‍💼 Usuario / Roles
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String ROLE_NOT_FOUND = "ROLE_NOT_FOUND";
    public static final String EMAIL_ALREADY_EXISTS = "EMAIL_ALREADY_EXISTS";
    
    // ⚙️ Infra / Integraciones
    public static final String DATABASE_UNAVAILABLE = "DATABASE_UNAVAILABLE";
    public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    public static final String INTEGRATION_ERROR = "INTEGRATION_ERROR";
    public static final String STORAGE_ERROR = "STORAGE_ERROR";
    
    // 🌐 Genéricos
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final String CONFLICT = "CONFLICT";
    public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
}
