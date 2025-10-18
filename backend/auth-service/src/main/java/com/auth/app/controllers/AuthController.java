package com.auth.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;
import com.auth.app.dto.request.ChangePasswordRequest;
import com.auth.app.dto.request.ForgotPasswordRequest;
import com.auth.app.dto.request.LoginRequest;
import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.request.ResetPasswordRequest;
import com.auth.app.dto.response.ApiResponse;
import com.auth.app.dto.response.AuthResponse;
import com.auth.app.dto.response.UserResponse;
import com.auth.app.services.application.AuthService;
import com.auth.app.utils.ApiResponseFactory;
import com.auth.app.utils.RequestUtils;
import com.auth.app.utils.TokenUtils;

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
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        AuthResponse authResponse = authService.register(request, ipAddress, userAgent);

        return ApiResponseFactory.success(
                "User registered successfully. Verification email sent",
                authResponse
        );
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(@RequestParam String email,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.resendVerificationEmail(email, ipAddress, userAgent);
        return ApiResponseFactory.success("Verification email resent successfully");
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(
            @RequestParam("token") String token,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.verifyEmail(token, ipAddress, userAgent);
        return ApiResponseFactory.success("Email verified successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Validated @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        AuthResponse authResponse = authService.login(request, ipAddress, userAgent);

        return ApiResponseFactory.success("Login successful", authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Authorization") String authHeader,
            @CookieValue("refreshToken") String refreshCookie,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        String refreshToken = TokenUtils.extractRefreshToken(authHeader, refreshCookie);

        AuthResponse authResponse = authService.refresh(refreshToken, ipAddress, userAgent);

        return ApiResponseFactory.success("Tokens refreshed successfully", authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.logout(authHeader, ipAddress, userAgent);

        return ApiResponseFactory.success("Logout successful", null);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Validated @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.forgotPassword(request, ipAddress, userAgent);

        return ApiResponseFactory.success("If the email address is registered, a password reset link has been sent", null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Validated @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.resetPassword(request, ipAddress, userAgent);

        return ApiResponseFactory.success(
                "Your password has been reset successfully.",
                null
        );
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Validated @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.changePassword(authHeader, request, ipAddress, userAgent);

        return ApiResponseFactory.success(
                "Your password has been updated successfully.",
                null
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        UserResponse user = authService.getCurrentUser(authorizationHeader, ipAddress, userAgent);

        return ApiResponseFactory.success("User retrieved successfully.", user);
    }

}
