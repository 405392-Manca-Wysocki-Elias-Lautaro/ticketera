package com.auth.app.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth.app.dto.response.ApiError;
import com.auth.app.dto.response.ApiResponse;
import com.auth.app.exception.exceptions.AccountNotVerifiedException;
import com.auth.app.exception.exceptions.CommonPasswordException;
import com.auth.app.exception.exceptions.EmailAlreadyExistsException;
import com.auth.app.exception.exceptions.EntityNotFoundException;
import com.auth.app.exception.exceptions.InvalidCredentialsException;
import com.auth.app.exception.exceptions.InvalidOrUnknownTokenException;
import com.auth.app.exception.exceptions.InvalidRefreshTokenException;
import com.auth.app.exception.exceptions.SamePasswordException;
import com.auth.app.exception.exceptions.TokenAlreadyUsedException;
import com.auth.app.exception.exceptions.TokenExpiredException;
import com.auth.app.exception.exceptions.TooManyAttemptsException;
import com.auth.app.exception.exceptions.UserAlreadyVerifiedException;
import com.auth.app.exception.exceptions.WeakPasswordException;
import com.auth.app.utils.ApiErrorFactory;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleMissingRequestCookie(
            MissingRequestCookieException ex,
            HttpServletRequest request) {

        String cookieName = ex.getCookieName();

        if ("refreshToken".equals(cookieName)) {
            log.warn("Missing refreshToken cookie at {}: {}", request.getRequestURI(), ex.getMessage());
            return ApiErrorFactory.error(ErrorCatalog.MISSING_REFRESH_TOKEN, null);
        }

        log.warn("Missing required cookie '{}' at {}: {}", cookieName, request.getRequestURI(), ex.getMessage());

        Map<String, String> details = Map.of(
                "cookie", cookieName
        );

        return ApiErrorFactory.error(ErrorCatalog.MISSING_REQUIRED_COOKIE, details);
    }

    // ⚠️ Validaciones fallidas (@Valid en DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        f -> f.getField(),
                        f -> f.getDefaultMessage(),
                        (a, b) -> b
                ));

        log.warn("Validation failed at {}: {}", request.getRequestURI(), errors);

        return ApiErrorFactory.error(ErrorCatalog.VALIDATION_FAILED, errors);
    }

    // 🚫 Violación de constraints (ej: UNIQUE email)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleConstraint(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        log.error("Constraint violation at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.CONFLICT, null);
    }

    // ✅ Excepciones custom de dominio
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleEmailExists(EmailAlreadyExistsException ex,
            HttpServletRequest request) {
        log.warn("Email already exists at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.EMAIL_ALREADY_EXISTS, null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleInvalidCredentials(InvalidCredentialsException ex,
            HttpServletRequest request) {
        log.warn("Invalid credentials at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.INVALID_CREDENTIALS, null);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleTokenExpired(TokenExpiredException ex,
            HttpServletRequest request) {
        log.warn("Expired token at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.TOKEN_EXPIRED, null);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleEntityNotFound(EntityNotFoundException ex,
            HttpServletRequest request) {
        log.warn("{} not found at {}: {}", ex.getEntityName(), request.getRequestURI(), ex.getMessage());

        Map<String, String> details = Map.of(
                "entity", ex.getEntityName(),
                "field", ex.getField(),
                "value", String.valueOf(ex.getValue())
        );

        return ApiErrorFactory.error(ex.getError(), details);
    }

    @ExceptionHandler(WeakPasswordException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleWeakPassword(WeakPasswordException ex,
            HttpServletRequest request) {
        log.warn("Weak password: {}", ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.WEAK_PASSWORD, null);
    }

    @ExceptionHandler(CommonPasswordException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleCommonPassword(CommonPasswordException ex,
            HttpServletRequest request) {
        log.warn("Common password rejected at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.COMMON_PASSWORD, null);
    }

    @ExceptionHandler(UserAlreadyVerifiedException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleUserAlreadyVerified(UserAlreadyVerifiedException ex,
            HttpServletRequest request) {
        log.warn("User already verified at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.USER_ALREADY_VERIFIED, null);
    }

    @ExceptionHandler(InvalidOrUnknownTokenException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleInvalidOrUnknownToken(InvalidOrUnknownTokenException ex,
            HttpServletRequest request) {
        log.warn("Invalid or unknown verification token at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.INVALID_OR_UNKNOWN_TOKEN, null);
    }

    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleTokenAlreadyUsed(TokenAlreadyUsedException ex,
            HttpServletRequest request) {
        log.warn("Token already used at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.TOKEN_ALREADY_USED, null);
    }

    @ExceptionHandler(AccountNotVerifiedException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleAccountNotVerified(AccountNotVerifiedException ex,
            HttpServletRequest request) {
        log.warn("Account not verified at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.ACCOUNT_NOT_VERIFIED, null);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleInvalidRefreshToken(InvalidRefreshTokenException ex,
            HttpServletRequest request) {
        log.warn("Invalid refresh token at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.INVALID_REFRESH_TOKEN, null);
    }

    @ExceptionHandler(TooManyAttemptsException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleTooManyAttempts(TooManyAttemptsException ex,
            HttpServletRequest request) {
        log.warn("Too many login attempts at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.TOO_MANY_ATTEMPTS, null);
    }

    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleSamePassword(SamePasswordException ex,
            HttpServletRequest request) {
        log.warn("Password change rejected at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.SAME_PASSWORD, null);
    }

    // 💥 Fallback genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ApiError>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ApiErrorFactory.error(ErrorCatalog.INTERNAL_SERVER_ERROR, null);
    }
}
