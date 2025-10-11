package com.notification.app.exceptions;

import com.notification.app.dto.ApiError;
import com.notification.app.exceptions.custom.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    private ApiError buildError(ErrorCatalog catalog, String path) {
        return ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .status(catalog.getStatus().value())
                .errorCode(catalog.getCode())
                .message(catalog.getMessage())
                .path(path)
                .build();
    }

    private boolean isRestMode() {
        return "dev".equalsIgnoreCase(activeProfile) || "test".equalsIgnoreCase(activeProfile);
    }

    @ExceptionHandler(TemplateRenderException.class)
    public ResponseEntity<?> handleTemplateRender(TemplateRenderException ex, HttpServletRequest req) {
        log.error("❌ Template rendering error: {}", ex.getMessage(), ex);

        ErrorCatalog error = ErrorCatalog.TEMPLATE_RENDER_ERROR;

        return isRestMode()
                ? ResponseEntity.status(error.getStatus()).body(buildError(error, req.getRequestURI()))
                : ResponseEntity.ok().build(); // Rabbit: no response, solo log
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<?> handleEmailSend(EmailSendException ex, HttpServletRequest req) {
        log.error("📧 Email sending failed: {}", ex.getMessage(), ex);

        ErrorCatalog error = ErrorCatalog.EMAIL_SEND_FAILED;

        return isRestMode()
                ? ResponseEntity.status(error.getStatus()).body(buildError(error, req.getRequestURI()))
                : ResponseEntity.ok().build();
    }

    @ExceptionHandler(NotificationProcessingException.class)
    public ResponseEntity<?> handleNotificationProcessing(NotificationProcessingException ex, HttpServletRequest req) {
        log.error("⚙️ Notification processing error: {}", ex.getMessage(), ex);

        ErrorCatalog error = ErrorCatalog.NOTIFICATION_PROCESSING_ERROR;

        return isRestMode()
                ? ResponseEntity.status(error.getStatus()).body(buildError(error, req.getRequestURI()))
                : ResponseEntity.ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("💥 Unexpected error: {}", ex.getMessage(), ex);

        ErrorCatalog error = ErrorCatalog.INTERNAL_SERVER_ERROR;

        return isRestMode()
                ? ResponseEntity.status(error.getStatus()).body(buildError(error, req.getRequestURI()))
                : ResponseEntity.ok().build();
    }

    @ExceptionHandler(UnsupportedChannelException.class)
    public ResponseEntity<?> handleUnsupportedChannel(UnsupportedChannelException ex, HttpServletRequest req) {
        log.warn("⚠️ Unsupported notification channel: {}", ex.getMessage(), ex);

        ErrorCatalog error = ErrorCatalog.UNSUPPORTED_CHANNEL;

        return isRestMode()
                ? ResponseEntity.status(error.getStatus()).body(buildError(error, req.getRequestURI()))
                : ResponseEntity.ok().build();
    }

    @ExceptionHandler(InvalidNotificationChannelException.class)
    public ResponseEntity<?> handleInvalidNotificationChannel(InvalidNotificationChannelException ex, HttpServletRequest req) {
        log.warn("⚠️ Invalid notification channel: {}", ex.getMessage(), ex);
        ErrorCatalog error = ErrorCatalog.INVALID_NOTIFICATION_CHANNEL;
        return isRestMode()
                ? ResponseEntity.status(error.getStatus()).body(buildError(error, req.getRequestURI()))
                : ResponseEntity.ok().build();
    }

    @ExceptionHandler(UnsupportedNotificationTypeException.class)
    public ResponseEntity<?> handleUnsupportedNotificationType(UnsupportedNotificationTypeException ex, HttpServletRequest req) {
        log.warn("⚠️ Unsupported notification type: {}", ex.getMessage(), ex);
        ErrorCatalog error = ErrorCatalog.UNSUPPORTED_NOTIFICATION_TYPE;
        return isRestMode()
                ? ResponseEntity.status(error.getStatus()).body(buildError(error, req.getRequestURI()))
                : ResponseEntity.ok().build();
    }

    @ExceptionHandler(InvalidTemplateVariablesException.class)
    public ResponseEntity<?> handleInvalidTemplateVariables(InvalidTemplateVariablesException ex, HttpServletRequest req) {
        log.warn("⚠️ Invalid or missing template variables: {}", ex.getMessage(), ex);
        ErrorCatalog error = ErrorCatalog.INVALID_TEMPLATE_VARIABLES;
        return isRestMode()
                ? ResponseEntity.status(error.getStatus()).body(buildError(error, req.getRequestURI()))
                : ResponseEntity.ok().build();
    }

}
