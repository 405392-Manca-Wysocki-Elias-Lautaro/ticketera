package com.auth.app.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LogAction {

    // 🧍‍♂️ USER AUTHENTICATION
    USER_REGISTERED("AUTH_001", "User registered successfully"),
    USER_EMAIL_VERIFIED("AUTH_002", "User verified email address"),
    USER_LOGIN("AUTH_003", "User logged in successfully"),
    USER_LOGIN_FAILED("AUTH_004", "User login attempt failed"),
    USER_LOGOUT("AUTH_005", "User logged out"),
    USER_EMAIL_VERIFICATION_FAILED("AUTH_006", "User email verification failed (invalid or expired token)"),
    USER_VERIFICATION_EMAIL_RESENT("AUTH_007", "Verification email resent to user"),
    USER_LOGIN_BLOCKED("AUTH_008", "Login blocked due to unverified or inactive account"),
    USER_LOGOUT_OTHER_DEVICES("AUTH_009", "User logged out other devices"),
    // 🔐 TOKEN OPERATIONS
    ACCESS_TOKEN_GENERATED("TOKEN_001", "Access token generated"),
    REFRESH_TOKEN_ROTATED("TOKEN_002", "Refresh token rotated"),
    TOKEN_REVOKED("TOKEN_003", "Token revoked or invalidated"),
    TOKEN_REFRESHED("TOKEN_004", "Access token refreshed"),
    REFRESH_TOKEN_INVALID("TOKEN_005", "Invalid or expired refresh token"),
    REFRESH_TOKEN_USED("TOKEN_006", "The refresh token has already been used"),
    ACCESS_TOKEN_EXPIRED("TOKEN_007", "Access token expired or invalid"),
    TOKEN_VALIDATION_FAILED("TOKEN_008", "Token validation failed due to tampering or format error"),
    ALL_TOKENS_REVOKED("TOKEN_009", "All user tokens revoked"),
    ALL_TOKENS_REVOKED_EXCEPT_CURRENT("TOKEN_010", "All tokens revoked except the current one"),
    // ⚙️ PROFILE / SETTINGS
    USER_PROFILE_UPDATED("USER_001", "User profile updated"),
    USER_PASSWORD_CHANGED("USER_002", "User password changed"),
    USER_ROLE_CHANGED("USER_003", "User role changed"),
    USER_PASSWORD_CHANGE_FAILED("USER_004", "User password change failed"),
    USER_PASSWORD_RESET_REQUEST("USER_006", "User requested password reset"),
    USER_PASSWORD_RESET_COMPLETED("USER_007", "User password reset completed"),
    USER_PROFILE_FETCHED("USER_008", "User profile fetched"),
    USER_PASSWORD_RESET_FAILED("USER_009", "Password reset failed (token expired or already used)"),
    // 🛡️ SECURITY
    LOGIN_RATE_LIMITED("SEC_001", "Login temporarily blocked due to multiple failed attempts"),
    UNAUTHORIZED_ACCESS_ATTEMPT("SEC_002", "Unauthorized access attempt detected"),
    ACCOUNT_LOCKED("SEC_003", "User account locked"),
    ACCOUNT_UNLOCKED("SEC_004", "User account unlocked"),
    // 📧 NOTIFICATIONS
    VERIFICATION_EMAIL_SENT("MAIL_001", "Verification email sent"),
    PASSWORD_RESET_REQUEST_EMAIL_SENT("MAIL_002", "Password reset request email sent"),
    PASSWORD_RESET_SUCCESS_EMAIL_SENT("MAIL_003", "Password reset success email sent"),
    USER_WELCOME_EMAIL_SENT("MAIL_004", "User welcome email sent"),
    LOGIN_ALERT_EMAIL_SENT("MAIL_005", "Login alert email sent"),
    // 📧 NOTIFICATION ERRORS
    VERIFICATION_EMAIL_ERROR("MAIL_006", "Failed to send verification email"),
    PASSWORD_RESET_REQUEST_EMAIL_ERROR("MAIL_007", "Failed to send password reset request email"),
    PASSWORD_RESET_SUCCESS_EMAIL_ERROR("MAIL_008", "Failed to send password reset success email"),
    USER_WELCOME_EMAIL_ERROR("MAIL_009", "Failed to send user welcome email"),
    LOGIN_ALERT_EMAIL_ERROR("MAIL_010", "Failed to send login alert email"),
    NOTIFICATION_ERROR("MAIL_011", "Notification dispatch failed"),
    // ⚠️ SYSTEM EVENTS
    SYSTEM_ERROR("SYS_001", "Unexpected system error"),
    RABBITMQ_MESSAGE_SENT("SYS_002", "Message published to RabbitMQ"),
    RABBITMQ_MESSAGE_RECEIVED("SYS_003", "Message consumed from RabbitMQ"),
    SERVICE_STARTED("SYS_004", "Microservice started successfully"),
    SERVICE_STOPPED("SYS_005", "Microservice stopped");

    private final String code;
    private final String description;
}
