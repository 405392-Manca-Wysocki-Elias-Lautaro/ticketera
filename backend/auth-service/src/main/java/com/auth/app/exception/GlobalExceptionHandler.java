package com.auth.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth.app.exception.exceptions.EmailAlreadyExistsException;
import com.auth.app.exception.exceptions.InvalidCredentialsException;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import com.auth.app.exception.exceptions.TokenExpiredException;
import com.auth.app.exception.exceptions.UserNotFoundException;
import com.auth.app.exception.exceptions.WeakPasswordException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiError buildError(HttpStatus status, String code, String message,
            String path, Map<String, String> details) {
        return ApiError.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .errorCode(code)
                .message(message)
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

        return ResponseEntity.badRequest().body(
                buildError(HttpStatus.BAD_REQUEST,
                        ErrorCodes.VALIDATION_FAILED,
                        "Request validation failed",
                        request.getRequestURI(),
                        errors)
        );
    }

    // 🚫 Violación de constraints (ej: UNIQUE email)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Constraint violation at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildError(HttpStatus.CONFLICT,
                        ErrorCodes.CONFLICT,
                        "Duplicate or invalid data",
                        request.getRequestURI(),
                        null)
        );
    }

    // ✅ Excepciones custom de dominio
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleEmailExists(EmailAlreadyExistsException ex, HttpServletRequest request) {
        log.warn("Email already exists at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                buildError(HttpStatus.CONFLICT,
                        ErrorCodes.EMAIL_ALREADY_EXISTS,
                        ex.getMessage(),
                        request.getRequestURI(),
                        null)
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        log.warn("Invalid credentials at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildError(HttpStatus.UNAUTHORIZED,
                        ErrorCodes.INVALID_CREDENTIALS,
                        "Invalid email or password",
                        request.getRequestURI(),
                        null)
        );
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiError> handleTokenExpired(TokenExpiredException ex, HttpServletRequest request) {
        log.warn("Expired token at {}: {}", request.getRequestURI(), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                buildError(HttpStatus.UNAUTHORIZED,
                        ErrorCodes.TOKEN_EXPIRED,
                        "Access token expired",
                        request.getRequestURI(),
                        null)
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<String> handleWeakPassword(WeakPasswordException ex) {
        log.warn("Weak password: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // 💥 Fallback genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorCodes.INTERNAL_SERVER_ERROR,
                        "An unexpected error occurred",
                        request.getRequestURI(),
                        null)
        );
    }
}
