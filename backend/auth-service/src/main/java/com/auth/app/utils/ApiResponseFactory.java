package com.auth.app.utils;

import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.auth.app.dto.response.ApiResponse;

public class ApiResponseFactory {

    private ApiResponseFactory() {
        // Private constructor para evitar instanciación
    }

    // 🔹 Éxito sin data
    public static ResponseEntity<ApiResponse<Void>> success(String message) {
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(message)
                        .timestamp(OffsetDateTime.now())
                        .build()
        );
    }

    // 🔹 Éxito con data
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(
                ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .data(data)
                        .timestamp(OffsetDateTime.now())
                        .build()
        );
    }

    // 🔹 Creado (HTTP 201)
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<T>builder()
                        .success(true)
                        .message(message)
                        .data(data)
                        .timestamp(OffsetDateTime.now())
                        .build());
    }

    // 🔹 Sin contenido (HTTP 204)
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }
}
