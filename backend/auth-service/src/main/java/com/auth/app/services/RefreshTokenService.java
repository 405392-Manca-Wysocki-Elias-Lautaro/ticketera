package com.auth.app.services;

import com.auth.app.entity.RefreshToken;

public interface RefreshTokenService {

    public RefreshToken create(Long userId, String rawToken, String userAgent, String ipAddress);
    public void revokeAllByUser(Long userId);
    public boolean validate(Long userId, String rawToken);
}
