package com.auth.app.services.domain;

import com.auth.app.domain.model.UserModel;

public interface TrustedDevicesService {
    boolean isNewDevice(UserModel user, String ipAddress, String userAgent);
}
