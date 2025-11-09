package com.ticket.app.utils;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.ticket.app.dto.response.ApiError;
import com.ticket.app.dto.response.ApiResponse;
import com.ticket.app.exception.ErrorCatalog;

/**
 * ❌ Factory para construir errores estandarizados del módulo de Tickets
 */
public class ApiErrorFactory {

    private ApiErrorFactory() {}

    public static ResponseEntity<ApiResponse<ApiError>> error(ErrorCatalog error) {
        return error(error, null);
    }

    public static ResponseEntity<ApiResponse<ApiError>> error(ErrorCatalog error, Map<String, String> details) {
        ApiError apiError = ApiError.builder()
                .status(error.getStatus().value())
                .code(error.getCode())
                .message(error.getMessage())
                .details(details)
                .build();

        ApiResponse<ApiError> response = ApiResponse.<ApiError>builder()
                .success(false)
                .message("Error en la operación de Ticket")
                .data(apiError)
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.status(error.getStatus()).body(response);
    }
}
