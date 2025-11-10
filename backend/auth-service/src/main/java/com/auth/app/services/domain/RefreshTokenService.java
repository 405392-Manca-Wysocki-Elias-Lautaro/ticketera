package com.auth.app.services.domain;

import java.util.UUID;

import com.auth.app.domain.model.RefreshTokenModel;
import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

public interface RefreshTokenService {

    RefreshTokenModel create(UserModel user, UUID deviceId, IpAddress ipAddress, UserAgent userAgent, boolean remembered);

    void revokeByDevice(UserModel user, UUID deviceId, IpAddress ipAddress, UserAgent userAgent);

    void revokeAllByUser(UserModel user, IpAddress ipAddress, UserAgent userAgent);

    void revokeAllExceptCurrent(UserModel user, UUID currentDeviceId, IpAddress ipAddress, UserAgent userAgent);

    RefreshTokenModel rotateToken(UserModel user, UUID deviceId, IpAddress ipAddress, UserAgent userAgent, boolean remembered);

    RefreshTokenModel rotateFromRefresh(UserModel user, String currentRawToken, UUID deviceId, IpAddress ipAddress, UserAgent userAgent, boolean remembered);
    
    boolean validate(UserModel user, String rawToken);

    RefreshTokenModel findByTokenHash(String hashedToken);

    boolean hasValidTokenForDevice(UserModel user, UUID deviceId);
}
