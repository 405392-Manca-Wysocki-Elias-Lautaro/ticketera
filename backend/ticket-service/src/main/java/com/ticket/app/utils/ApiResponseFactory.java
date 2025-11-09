package com.ticket.app.utils;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import com.ticket.app.dto.response.ApiResponse;


/**
 * ✅ Factory para construir respuestas exitosas del módulo de Tickets
 */
public class ApiResponseFactory {

    private ApiResponseFactory() {
        // Evitar instanciación
    }

    // 🟢 Éxito sin data
    public static ResponseEntity<ApiResponse<Void>> success(String message) {
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(message)
                        .timestamp(OffsetDateTime.now())
                        .build()
        );
    }

    // 🟢 Éxito con data
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

    // 🟢 Created (HTTP 201)
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<T>builder()
                                .success(true)
                                .message(message)
                                .data(data)
                                .timestamp(OffsetDateTime.now())
                                .build()
                );
    }

    // 🟢 Multi-status con headers opcionales (ej. para descargas o streams)
    public static <T> ResponseEntity<ApiResponse<T>> withHeaders(String message, T data, HttpHeaders headers) {
        return ResponseEntity.ok()
                .headers(headers)
                .body(
                        ApiResponse.<T>builder()
                                .success(true)
                                .message(message)
                                .data(data)
                                .timestamp(OffsetDateTime.now())
                                .build()
                );
    }

    // 🟢 Éxito con cookies (por consistencia con auth)
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data, List<ResponseCookie> cookies) {
        HttpHeaders headers = new HttpHeaders();
        cookies.forEach(c -> headers.add(HttpHeaders.SET_COOKIE, c.toString()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(
                        ApiResponse.<T>builder()
                                .success(true)
                                .message(message)
                                .data(data)
                                .timestamp(OffsetDateTime.now())
                                .build()
                );
    }

    // 🟢 No Content
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }
}
