package com.auth.app.servicies;

import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.response.AuthResponse;

public interface AuthService {
    public AuthResponse register(RegisterRequest request, String userAgent, String ipAddress);
}