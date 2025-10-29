package com.auth.app.utils;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import com.auth.app.dto.response.ApiResponse;

public class ApiResponseFactory {

    private ApiResponseFactory() {
        // evitar instanciación
    }

    // ✅ Éxito sin data
    public static ResponseEntity<ApiResponse<Void>> success(String message) {
        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(message)
                        .timestamp(OffsetDateTime.now())
                        .build()
        );
    }

    // ✅ Éxito con data
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

    // ✅ Éxito con data + cookie única
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data, ResponseCookie cookie) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(
                        ApiResponse.<T>builder()
                                .success(true)
                                .message(message)
                                .data(data)
                                .timestamp(OffsetDateTime.now())
                                .build()
                );
    }

    // ✅ Éxito con data + múltiples cookies
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data, List<ResponseCookie> cookies) {
        var headers = new HttpHeaders();
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

    // ✅ Created (HTTP 201)
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

    // ✅ No Content (HTTP 204)
    public static ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }
}
