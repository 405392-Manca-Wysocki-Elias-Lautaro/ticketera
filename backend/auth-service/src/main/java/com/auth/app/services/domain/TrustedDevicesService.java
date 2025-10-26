package com.auth.app.services.domain;

import java.util.UUID;

import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

public interface TrustedDevicesService {
    boolean isNewDevice(UserModel user, UUID deviceId, IpAddress ipAddress, UserAgent userAgent);
    void unregisterCurrentDevice(UserModel user, UUID deviceId);
    void unregisterAllExceptCurrent(UserModel user, UUID currentDeviceId);
}
