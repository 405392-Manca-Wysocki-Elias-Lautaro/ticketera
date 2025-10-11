package com.auth.app.services.domain;

import java.util.UUID;

public interface RefreshTokenService {

    void create(UUID userId, String rawToken, String userAgent, String ipAddress, boolean remembered);
    void revokeAllByUser(UUID userId);
    boolean validate(UUID userId, String rawToken);
}
