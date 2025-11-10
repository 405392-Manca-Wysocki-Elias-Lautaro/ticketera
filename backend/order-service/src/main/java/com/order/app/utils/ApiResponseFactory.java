package com.order.app.utils;

import com.order.app.pkg.dtos.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

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
    
    // 🔹 Error genérico
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.<T>builder()
                        .success(false)
                        .message(message)
                        .timestamp(OffsetDateTime.now())
                        .build());
    }
    
    // 🔹 Error 400 Bad Request
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }
    
    // 🔹 Error 404 Not Found
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }
    
    // 🔹 Error 500 Internal Server Error
    public static <T> ResponseEntity<ApiResponse<T>> internalError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

