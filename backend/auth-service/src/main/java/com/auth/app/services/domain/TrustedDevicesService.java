package com.auth.app.services.domain;

import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

public interface TrustedDevicesService {
    boolean isNewDevice(UserModel user, IpAddress ipAddress, UserAgent userAgent);
    void unregisterCurrentDevice(UserModel user, IpAddress ipAddress, UserAgent userAgent);
    void unregisterAllExceptCurrent(UserModel user, IpAddress ipAddress, UserAgent userAgent);
}
