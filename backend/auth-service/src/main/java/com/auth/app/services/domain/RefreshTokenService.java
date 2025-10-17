package com.auth.app.services.domain;

import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

public interface RefreshTokenService {

    String create(UserModel user, IpAddress ipAddress, UserAgent userAgent, boolean remembered);
    void revokeAllByUser(UserModel user);
    String rotateToken(UserModel user, IpAddress ipAddress, UserAgent userAgent, boolean remembered);
    boolean validate(UserModel user, String rawToken, IpAddress ipAddress, UserAgent userAgent);
    String rotateFromRefresh(UserModel user, String currentRawToken, IpAddress ipAddress, UserAgent userAgent, boolean remembered);
}
