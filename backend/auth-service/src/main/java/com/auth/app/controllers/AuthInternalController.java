package com.auth.app.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.app.dto.response.ApiResponse;
import com.auth.app.dto.response.UserResponse;
import com.auth.app.services.application.AuthService;
import com.auth.app.utils.ApiResponseFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Slf4j
public class AuthInternalController {

    private final AuthService authService;
    private static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";
    private static final String INTERNAL_SECRET_VALUE = "TICKETERA_INTERNAL_SECRET_2024";

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable UUID id,
            @RequestHeader(value = INTERNAL_SECRET_HEADER, required = false) String secret) {

        if (!INTERNAL_SECRET_VALUE.equals(secret)) {
            log.warn("Unauthorized access attempt to internal auth endpoint");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        UserResponse user = authService.getUserById(id);
        if (user == null) {
            return ApiResponseFactory.notFound("User not found");
        }

        return ApiResponseFactory.success("User found", user);
    }
}
