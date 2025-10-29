package com.auth.app.controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth.app.domain.model.AuthModel;
import com.auth.app.domain.model.RefreshTokenModel;
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
import com.auth.app.exception.exceptions.InvalidOrUnknownTokenException;
import com.auth.app.exception.exceptions.InvalidRefreshTokenException;
import com.auth.app.services.application.AuthService;
import com.auth.app.utils.ApiResponseFactory;
import com.auth.app.utils.RequestUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Validated @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        AuthModel authModel = authService.register(request, ipAddress, userAgent);
        AuthResponse authResponse = modelMapper.map(authModel, AuthResponse.class);

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
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Validated @RequestBody LoginRequest request,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceIdHeader,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);
        UUID deviceId = Optional.ofNullable(deviceIdHeader).map(UUID::fromString).orElse(UUID.randomUUID());

        AuthModel authModel = authService.login(request, deviceId, ipAddress, userAgent);
        RefreshTokenModel refreshToken = authModel.getRefreshToken();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false) //TODO: PONER EN TRUE EN PRODUCCION // ⚠️ si estás en HTTP local, ponelo false para testear
                .sameSite("None") // necesario si el frontend está en otro dominio
                .path("/")
                .maxAge(Duration.between(OffsetDateTime.now(), refreshToken.getExpiresAt()))
                .build();

        AuthResponse authResponse = modelMapper.map(authModel, AuthResponse.class);

        return ApiResponseFactory.success("Login successful", authResponse, refreshCookie);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @CookieValue(value = "refreshToken") String refreshCookie,
            @RequestHeader(value = "X-Device-Id", required = false) String deviceIdHeader,
            HttpServletRequest httpRequest
    ) {

        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        if (refreshCookie == null || refreshCookie.isBlank()) {
            log.warn("[REFRESH_TOKEN] Missing or empty refreshToken cookie");
            throw new InvalidRefreshTokenException();
        }

        String refreshToken = URLDecoder.decode(refreshCookie, StandardCharsets.UTF_8);
        log.info("[REFRESH_TOKEN] Received refresh token (cookie): {}...", refreshToken.substring(0, 10));

        if (refreshToken.split("\\.").length == 3) {
            log.warn("[REFRESH_TOKEN] JWT detected — expected opaque token");
            throw new InvalidRefreshTokenException();
        }

        UUID deviceId = Optional.ofNullable(deviceIdHeader)
                .map(UUID::fromString)
                .orElseGet(() -> {
                    UUID randomId = UUID.randomUUID();
                    log.warn("[REFRESH_TOKEN] Missing X-Device-Id header — generated random: {}", randomId);
                    return randomId;
                });

        AuthModel authModel = authService.refresh(refreshToken, deviceId, ipAddress, userAgent);
        RefreshTokenModel rotatedRefresh = authModel.getRefreshToken();

        ResponseCookie newCookie = ResponseCookie.from("refreshToken", rotatedRefresh.getToken())
                .httpOnly(true)
                .secure(false) // ⚠️ poné true en producción (HTTPS obligatorio)
                .sameSite("None") // o "None" si tu frontend está en otro dominio HTTPS
                .path("/")
                .maxAge(Duration.between(OffsetDateTime.now(), rotatedRefresh.getExpiresAt()))
                .build();

        AuthResponse authResponse = modelMapper.map(authModel, AuthResponse.class);

        return ApiResponseFactory.success("Tokens refreshed successfully", authResponse, newCookie);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authHeader,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        authService.logout(authHeader, ipAddress, userAgent);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) //TODO: PONER EN TRUE EN PRODUCCION //
                .sameSite("None")
                .path("/")
                .maxAge(0) // elimina inmediatamente
                .build();

        return ApiResponseFactory.success("Logout successful", null, deleteCookie);
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
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest
    ) {
        IpAddress ipAddress = RequestUtils.extractIp(httpRequest);
        UserAgent userAgent = RequestUtils.extractUserAgent(httpRequest);

        UserResponse user = authService.getCurrentUser(authorizationHeader, ipAddress, userAgent);

        return ApiResponseFactory.success("User retrieved successfully.", user);
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Void>> validateToken(@RequestParam("token") String token) {
        if (!authService.validateAccessToken(token)) {
            throw new InvalidOrUnknownTokenException();
        }

        return ApiResponseFactory.success("Token validated.");
    }
}
