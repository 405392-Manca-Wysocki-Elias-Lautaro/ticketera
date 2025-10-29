package com.auth.app.utils;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.auth.app.dto.response.ApiError;
import com.auth.app.dto.response.ApiResponse;
import com.auth.app.exception.ErrorCatalog;

public class ApiErrorFactory {

    private ApiErrorFactory() {}

    public static ResponseEntity<ApiResponse<ApiError>> error(ErrorCatalog error, Map<String, String> details) {
        ApiError apiError = ApiError.builder()
                .status(error.getStatus().value())
                .code(error.getCode())
                .message(error.getMessage())
                .details(details)
                .build();

        ApiResponse<ApiError> response = ApiResponse.<ApiError>builder()
                .success(false)
                .message("Error en la solicitud")
                .data(apiError)
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.status(error.getStatus()).body(response);
    }
}
