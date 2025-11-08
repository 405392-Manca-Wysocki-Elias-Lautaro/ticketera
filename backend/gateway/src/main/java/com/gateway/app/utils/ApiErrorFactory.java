package com.gateway.app.utils;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.gateway.app.dto.ApiError;
import com.gateway.app.dto.ApiResponse;

public class ApiErrorFactory {

    private ApiErrorFactory() {}

    public static ResponseEntity<ApiResponse<ApiError>> error(HttpStatus status, String code, String message, Map<String, String> details) {
        ApiError apiError = ApiError.builder()
                .status(status.value())
                .code(code)
                .message(message)
                .details(details)
                .build();

        ApiResponse<ApiError> response = ApiResponse.<ApiError>builder()
                .success(false)
                .message("Request error")
                .data(apiError)
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);
    }

    // Overload sin details
    public static ResponseEntity<ApiResponse<ApiError>> error(HttpStatus status, String code, String message) {
        return error(status, code, message, null);
    }
}
