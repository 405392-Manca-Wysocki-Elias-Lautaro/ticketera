package com.auth.app.controllers;

import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;
import com.auth.app.dto.request.*;
import com.auth.app.dto.response.*;
import com.auth.app.exception.exceptions.InvalidOrUnknownTokenException;
import com.auth.app.services.application.AuthService;
import com.auth.app.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Validated @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        AuthResponse authResponse = authService.register(request, ipAddress, userAgent);

        return ApiResponseFactory.success("User registered successfully. Verification email sent", authResponse);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.resendVerificationEmail(email, ipAddress, userAgent);

        return ApiResponseFactory.success("Verification email resent successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(@RequestParam("token") String token, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.verifyEmail(token, ipAddress, userAgent);

        return ApiResponseFactory.success("Email verified successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Validated @RequestBody LoginRequest request, @RequestHeader(value = "X-Device-Id", required = false) String deviceIdHeader, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        UUID deviceId = Optional.ofNullable(deviceIdHeader).map(UUID::fromString).orElse(UUID.randomUUID());
        AuthResponse authResponse = authService.login(request, deviceId, ipAddress, userAgent);

        return ApiResponseFactory.success("Login successful", authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestHeader("Authorization") String authHeader, @CookieValue("refreshToken") String refreshCookie, @RequestHeader(value = "X-Device-Id", required = false) String deviceIdHeader, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        String refreshToken = TokenUtils.extractRefreshToken(authHeader, refreshCookie);

        UUID deviceId = Optional.ofNullable(deviceIdHeader).map(UUID::fromString).orElse(UUID.randomUUID());
        AuthResponse authResponse = authService.refresh(refreshToken, deviceId, ipAddress, userAgent);

        return ApiResponseFactory.success("Tokens refreshed successfully", authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader("Authorization") String authHeader, @RequestHeader(value = "X-Device-Id", required = false) String deviceIdHeader, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        UUID deviceId = Optional.ofNullable(deviceIdHeader).map(UUID::fromString).orElse(UUID.randomUUID());

        authService.logout(authHeader, deviceId, ipAddress, userAgent);

        return ApiResponseFactory.success("Logout successful", null);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Validated @RequestBody ForgotPasswordRequest request, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.forgotPassword(request, ipAddress, userAgent);

        return ApiResponseFactory.success("If the email address is registered, a password reset link has been sent", null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Validated @RequestBody ResetPasswordRequest request, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.resetPassword(request, ipAddress, userAgent);

        return ApiResponseFactory.success("Your password has been reset successfully.", null);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@RequestHeader("Authorization") String authHeader, @Validated @RequestBody ChangePasswordRequest request, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.changePassword(authHeader, request, ipAddress, userAgent);

        return ApiResponseFactory.success("Your password has been updated successfully.", null);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@RequestHeader("Authorization") String authorizationHeader, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        UserResponse user = authService.getCurrentUser(authorizationHeader, ipAddress, userAgent);

        return ApiResponseFactory.success("User retrieved successfully.", user);
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Void>> validateToken(@RequestParam("token") String token) {
        if (!authService.validateAccessToken(token)) throw new InvalidOrUnknownTokenException();
        
        return ApiResponseFactory.success("Token validated.");
    }
}
