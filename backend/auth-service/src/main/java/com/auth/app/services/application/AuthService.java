package com.auth.app.services.application;

import com.auth.app.dto.request.ChangePasswordRequest;
import com.auth.app.dto.request.ForgotPasswordRequest;
import com.auth.app.dto.request.LoginRequest;
import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.request.ResetPasswordRequest;
import com.auth.app.dto.response.AuthResponse;


public interface AuthService {
    AuthResponse register(RegisterRequest request, String ipAddress, String userAgent);
    void resendVerificationEmail(String email);
    void verifyEmail(String rawToken, String ipAddress, String userAgent);
    AuthResponse login(LoginRequest request, String ipAddress, String userAgent);
    AuthResponse refresh(String rawRefreshToken, String ipAddress, String userAgent);
    void logout(String authorizationHeader, String ipAddress, String userAgent);
    void changePassword(String authorizationHeader, ChangePasswordRequest request, String ipAddress, String userAgent);
    void forgotPassword(ForgotPasswordRequest request, String ipAddress, String userAgent);
    void resetPassword(ResetPasswordRequest request, String ipAddress, String userAgent);
}