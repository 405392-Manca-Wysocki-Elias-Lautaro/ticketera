package com.ticket.app.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ticket.app.dto.response.ApiError;
import com.ticket.app.dto.response.ApiResponse;
import com.ticket.app.exception.exceptions.*;
import com.ticket.app.utils.ApiErrorFactory;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "com.ticket.app")
public class GlobalExceptionHandler {

    // ⚠️ Validaciones DTO
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

    // 🚫 Violaciones de constraints (ej: UNIQUE code)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleConstraint(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        log.error("Constraint violation at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.CONFLICT, null);
    }

    // 🎟️ Excepciones de dominio
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleTicketNotFound(TicketNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Ticket not found at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.TICKET_NOT_FOUND, null);
    }

    @ExceptionHandler(TicketAlreadyCheckedInException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleAlreadyCheckedIn(TicketAlreadyCheckedInException ex,
            HttpServletRequest request) {
        log.warn("Ticket already checked-in at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.TICKET_ALREADY_CHECKED_IN, null);
    }

    @ExceptionHandler(InvalidTicketStatusException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleInvalidStatus(InvalidTicketStatusException ex,
            HttpServletRequest request) {
        log.warn("Invalid ticket status at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.INVALID_TICKET_STATUS, null);
    }

    @ExceptionHandler(InvalidQrTokenException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleInvalidQr(InvalidQrTokenException ex,
            HttpServletRequest request) {
        log.warn("Invalid QR token at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.INVALID_QR_TOKEN, null);
    }

    @ExceptionHandler(TicketExpiredException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleExpired(TicketExpiredException ex,
            HttpServletRequest request) {
        log.warn("Expired ticket at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.EXPIRED_TICKET, null);
    }

    @ExceptionHandler(InvalidTicketValidationTypeException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleInvalidType(InvalidTicketValidationTypeException ex,
            HttpServletRequest request) {
        log.warn("Invalid validation type at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.INVALID_TICKET_VALIDATION_TYPE, null);
    }

    @ExceptionHandler(SeatAlreadyHeldException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleSeatAlreadyHeld(SeatAlreadyHeldException ex,
            HttpServletRequest request) {
        log.warn("Seat already held at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.SEAT_ALREADY_HELD, null);
    }

    @ExceptionHandler(InvalidHoldQuantityException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleInvalidHoldQuantity(InvalidHoldQuantityException ex,
            HttpServletRequest request) {
        log.warn("Invalid hold quantity at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.INVALID_HOLD_QUANTITY, null);
    }

    @ExceptionHandler(HoldConversionException.class)
    public ResponseEntity<ApiResponse<ApiError>> handleHoldConversion(HoldConversionException ex,
            HttpServletRequest request) {
        log.warn("Hold conversion error at {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiErrorFactory.error(ErrorCatalog.HOLD_CONVERSION_ERROR, null);
    }

    // 💥 Fallback genérico
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ApiError>> handleGeneric(Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ApiErrorFactory.error(ErrorCatalog.INTERNAL_SERVER_ERROR, null);
    }
}
