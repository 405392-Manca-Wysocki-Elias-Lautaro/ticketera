package com.auth.app.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCatalog {

    // 🔐 Auth / Security
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_CREDENTIALS,
            "Invalid email or password."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED,
            "You are not authorized to access this resource."),
    FORBIDDEN(HttpStatus.FORBIDDEN, ErrorCodes.FORBIDDEN,
            "You do not have sufficient permissions."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorCodes.TOKEN_EXPIRED,
            "Access token has expired."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_TOKEN,
            "Access token is invalid."),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, ErrorCodes.TOO_MANY_ATTEMPTS,
            "Too many failed attempts. Please try again later."),
    ACCOUNT_NOT_VERIFIED(HttpStatus.UNAUTHORIZED, ErrorCodes.ACCOUNT_NOT_VERIFIED,
            "Please verify your email address before signing in."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_REFRESH_TOKEN,
            "Invalid or expired refresh token."),
    // 📥 Validations
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, ErrorCodes.VALIDATION_FAILED,
            "One or more fields do not meet the required criteria."),
    MISSING_FIELDS(HttpStatus.BAD_REQUEST, ErrorCodes.MISSING_FIELDS,
            "Required fields are missing."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, ErrorCodes.INVALID_FORMAT,
            "One or more fields have an invalid format."),
    WEAK_PASSWORD(HttpStatus.BAD_REQUEST, ErrorCodes.WEAK_PASSWORD,
            "The password does not meet security requirements."),
    COMMON_PASSWORD(HttpStatus.BAD_REQUEST, ErrorCodes.COMMON_PASSWORD,
            "The chosen password is too common or insecure."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, ErrorCodes.SAME_PASSWORD,
            "The new password cannot be the same as the previous one."),
    // 🧑‍💼 User / Roles
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, ErrorCodes.USER_ALREADY_EXISTS,
            "User already exists."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, ErrorCodes.EMAIL_ALREADY_EXISTS,
            "Email address is already registered."),
    // ✅ Email Verification
    USER_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, ErrorCodes.USER_ALREADY_VERIFIED,
            "User account has already been verified."),
    INVALID_OR_UNKNOWN_TOKEN(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_OR_UNKNOWN_TOKEN,
            "The provided token is invalid or does not correspond to any user."),
    TOKEN_ALREADY_USED(HttpStatus.BAD_REQUEST, ErrorCodes.TOKEN_ALREADY_USED,
            "The verification token has already been used."),
    EMAIL_VERIFICATION_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, ErrorCodes.EMAIL_VERIFICATION_TOKEN_EXPIRED,
            "The verification token has expired."),
    // ⚙️ Infrastructure / Integrations
    DATABASE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodes.DATABASE_UNAVAILABLE,
            "Database is currently unavailable."),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, ErrorCodes.SERVICE_UNAVAILABLE,
            "Service is temporarily unavailable. Please try again later."),
    INTEGRATION_ERROR(HttpStatus.BAD_GATEWAY, ErrorCodes.INTEGRATION_ERROR,
            "Error occurred while communicating with an external service."),
    STORAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.STORAGE_ERROR,
            "Error accessing storage system."),
    // 🌐 Generic
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorCodes.ENTITY_NOT_FOUND,
            "Requested resource not found."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, ErrorCodes.BAD_REQUEST,
            "Invalid request."),
    CONFLICT(HttpStatus.CONFLICT, ErrorCodes.CONFLICT,
            "Conflict with the submitted data."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_SERVER_ERROR,
            "Internal server error.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCatalog(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
