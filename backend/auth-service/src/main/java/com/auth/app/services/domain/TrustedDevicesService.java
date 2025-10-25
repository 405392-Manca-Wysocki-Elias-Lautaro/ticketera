package com.auth.app.services.domain;

import java.util.UUID;
import com.auth.app.domain.model.UserModel;

public interface TrustedDevicesService {
    boolean isNewDevice(UserModel user, UUID deviceId);
    void unregisterCurrentDevice(UserModel user, UUID deviceId);
    void unregisterAllExceptCurrent(UserModel user, UUID currentDeviceId);
}
