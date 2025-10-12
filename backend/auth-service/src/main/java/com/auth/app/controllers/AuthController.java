package com.auth.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth.app.dto.request.LoginRequest;
import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.response.ApiResponse;
import com.auth.app.dto.response.AuthResponse;
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

        AuthResponse authResponse = authService.register(request, ipAddress, userAgent);

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

        authService.verifyEmail(token, ipAddress, userAgent);
        return ApiResponseFactory.success("Email verified successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Validated @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse authResponse = authService.login(request, ip, userAgent);

        return ApiResponseFactory.success("Login successful.", authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest request
    ) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        String refreshToken = authorizationHeader.replace("Bearer ", "").trim();

        AuthResponse authResponse = authService.refresh(refreshToken, ipAddress, userAgent);

        return ApiResponseFactory.success("Tokens refreshed successfully.", authResponse);
    }
}
