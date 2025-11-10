package com.event.app.exceptions;

import com.event.app.dtos.response.ApiResponse;
import com.event.app.utils.ApiResponseFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OrganizerNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOrganizerNotFound(OrganizerNotFoundException ex) {
        return ApiResponseFactory.notFound(ex.getMessage());
    }

    @ExceptionHandler(VenueNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleVenueNotFound(VenueNotFoundException ex) {
        return ApiResponseFactory.notFound(ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return ApiResponseFactory.notFound(ex.getMessage());
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEventNotFound(EventNotFoundException ex) {
        return ApiResponseFactory.notFound(ex.getMessage());
    }

    @ExceptionHandler(OccurrenceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleOccurrenceNotFound(OccurrenceNotFoundException ex) {
        return ApiResponseFactory.notFound(ex.getMessage());
    }

    @ExceptionHandler(VenueAreaNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleVenueAreaNotFound(VenueAreaNotFoundException ex) {
        return ApiResponseFactory.notFound(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .data(errors)
                        .timestamp(java.time.OffsetDateTime.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        return ApiResponseFactory.internalError("An unexpected error occurred: " + ex.getMessage());
    }

}
