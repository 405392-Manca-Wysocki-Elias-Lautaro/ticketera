package com.auth.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.response.AuthResponse;
import com.auth.app.dto.response.ApiResponse;
import com.auth.app.services.application.AuthService;
import com.auth.app.utils.ApiResponseFactory;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Validated @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        String userAgent = httpRequest.getHeader("User-Agent");
        String ipAddress = httpRequest.getRemoteAddr();

        AuthResponse authResponse = authService.register(request, userAgent, ipAddress);

        return ApiResponseFactory.success(
                "User registered successfully. Verification email sent.",
                authResponse
        );
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ApiResponseFactory.success("Verification email resent successfully.");
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(
            @RequestParam("token") String token,
            HttpServletRequest httpRequest
    ) {
        String userAgent = httpRequest.getHeader("User-Agent");
        String ipAddress = httpRequest.getRemoteAddr();

        authService.verifyEmail(token, userAgent, ipAddress);
        return ApiResponseFactory.success("Email verified successfully.");
    }
}
