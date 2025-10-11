package com.auth.app.services.application;

import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.response.AuthResponse;


public interface AuthService {
    AuthResponse register(RegisterRequest request, String userAgent, String ipAddress);
    void verifyEmail(String rawToken, String ipAddress, String userAgent) ;
}