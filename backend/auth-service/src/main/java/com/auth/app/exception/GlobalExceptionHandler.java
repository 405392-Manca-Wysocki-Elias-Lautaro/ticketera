package com.auth.app.exception;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth.app.exception.exceptions.EmailAlreadyExistsException;
import com.auth.app.exception.exceptions.EntityNotFoundException;
import com.auth.app.exception.exceptions.InvalidCredentialsException;
import com.auth.app.exception.exceptions.InvalidOrUnknownTokenException;
import com.auth.app.exception.exceptions.TokenAlreadyUsedException;
import com.auth.app.exception.exceptions.TokenExpiredException;
import com.auth.app.exception.exceptions.UserAlreadyVerifiedException;
import com.auth.app.exception.exceptions.WeakPasswordException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildError(ErrorCatalog error, String path, Map<String, String> details) {
        return ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .status(error.getStatus().value())
                .errorCode(error.getCode())
                .message(error.getMessage())
                .path(path)
                .details(details)
                .build();
    }

    // ⚠️ Validaciones fallidas (@Valid en DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> f.getDefaultMessage(),
                        (a, b) -> b
                ));

        log.warn("Validation failed at {}: {}", request.getRequestURI(), errors);

        ErrorCatalog error = ErrorCatalog.VALIDATION_FAILED;

        return ResponseEntity.status(error.getStatus())
                .body(buildError(error, request.getRequestURI(), errors));
    }

    // 🚫 Violación de constraints (ej: UNIQUE email)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Constraint violation at {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorCatalog error = ErrorCatalog.CONFLICT;
        return ResponseEntity.status(error.getStatus())
                .body(buildError(error, request.getRequestURI(), null));
    }

    // ✅ Excepciones custom de dominio
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailExists(EmailAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Email already exists at {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorCatalog error = ErrorCatalog.EMAIL_ALREADY_EXISTS;
        return ResponseEntity.status(error.getStatus())
                .body(buildError(error, request.getRequestURI(), null));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        log.warn("Invalid credentials at {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorCatalog error = ErrorCatalog.INVALID_CREDENTIALS;
        return ResponseEntity.status(error.getStatus())
                .body(buildError(error, request.getRequestURI(), null));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpired(TokenExpiredException ex, HttpServletRequest request) {
        log.warn("Expired token at {}: {}", request.getRequestURI(), ex.getMessage());

        ErrorCatalog error = ErrorCatalog.TOKEN_EXPIRED;
        return ResponseEntity.status(error.getStatus())
                .body(buildError(error, request.getRequestURI(), null));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("{} not found at {}: {}", ex.getEntityName(), request.getRequestURI(), ex.getMessage());

        ErrorCatalog error = ex.getError();
        Map<String, String> details = Map.of(
                "entity", ex.getEntityName(),
                "field", ex.getField(),
                "value", String.valueOf(ex.getValue())
        );

        return ResponseEntity.status(error.getStatus())
                .body(ApiError.builder()
                        .timestamp(java.time.OffsetDateTime.now())
                        .status(error.getStatus().value())
                        .errorCode(error.getCode())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .details(details)
                        .build());
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<ApiError> handleWeakPassword(WeakPasswordException ex, HttpServletRequest request) {
        log.warn("Weak password: {}", ex.getMessage());

        ErrorCatalog error = ex.getMessage().equals(ErrorCodes.COMMON_PASSWORD)
                ? ErrorCatalog.COMMON_PASSWORD
                : ErrorCatalog.WEAK_PASSWORD;

        return ResponseEntity.status(error.getStatus())
                .body(buildError(error, request.getRequestURI(), null));
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ApiError> handleUserAlreadyVerified(UserAlreadyVerifiedException ex, HttpServletRequest request) {
        log.warn("User already verified at {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorCatalog error = ErrorCatalog.USER_ALREADY_VERIFIED;
        return ResponseEntity.status(error.getStatus())
                .body(buildError(error, request.getRequestURI(), null));
    }

    @ExceptionHandler(InvalidOrUnknownTokenException.class)
    public ResponseEntity<ApiError> handleInvalidOrUnknownToken(InvalidOrUnknownTokenException ex, HttpServletRequest request) {
        log.warn("Invalid or unknown verification token at {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorCatalog error = ErrorCatalog.INVALID_OR_UNKNOWN_TOKEN;
        return ResponseEntity.status(error.getStatus())
                .body(buildError(error, request.getRequestURI(), null));
    }

    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<ApiError> handleTokenAlreadyUsed(TokenAlreadyUsedException ex, HttpServletRequest request) {
        log.warn("Token already used at {}: {}", request.getRequestURI(), ex.getMessage());
        ErrorCatalog error = ErrorCatalog.TOKEN_ALREADY_USED;
        return ResponseEntity.status(error.getStatus())
                .body(buildError(error, request.getRequestURI(), null));
    }

    // 💥 Fallback genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorCatalog error = ErrorCatalog.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(error.getStatus())
                .body(buildError(error, request.getRequestURI(), null));
    }
}
