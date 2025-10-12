package com.auth.app.services.domain;

import com.auth.app.domain.model.UserModel;

public interface RefreshTokenService {

    String create(UserModel user, String ipAddress, String userAgent, boolean remembered);
    void revokeAllByUser(UserModel user);
    String rotateToken(UserModel user, String ipAddress, String userAgent, boolean remembered);
    boolean validate(UserModel user, String rawToken, String ipAddress, String userAgent);
    String rotateFromRefresh(UserModel user, String currentRawToken, String ipAddress, String userAgent, boolean remembered);
}
