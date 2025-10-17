package com.auth.app.services.application;

import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;
import com.auth.app.dto.request.ChangePasswordRequest;
import com.auth.app.dto.request.ForgotPasswordRequest;
import com.auth.app.dto.request.LoginRequest;
import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.request.ResetPasswordRequest;
import com.auth.app.dto.response.AuthResponse;
import com.auth.app.dto.response.UserResponse;


public interface AuthService {
    AuthResponse register(RegisterRequest request, IpAddress ipAddress, UserAgent userAgent);
    void resendVerificationEmail(String email, IpAddress ipAddress, UserAgent userAgent);
    void verifyEmail(String rawToken, IpAddress ipAddress, UserAgent userAgent);
    AuthResponse login(LoginRequest request, IpAddress ipAddress, UserAgent userAgent);
    AuthResponse refresh(String rawRefreshToken, IpAddress ipAddress, UserAgent userAgent);
    void logout(String authorizationHeader, IpAddress ipAddress, UserAgent userAgent);
    void changePassword(String authorizationHeader, ChangePasswordRequest request, IpAddress ipAddress, UserAgent userAgent);
    void forgotPassword(ForgotPasswordRequest request, IpAddress ipAddress, UserAgent userAgent);
    void resetPassword(ResetPasswordRequest request, IpAddress ipAddress, UserAgent userAgent);
    UserResponse getCurrentUser(String authorizationHeader, IpAddress ipAddress, UserAgent userAgent);
}