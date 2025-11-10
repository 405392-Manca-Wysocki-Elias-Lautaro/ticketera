package com.auth.app.domain.events;

import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLoginFromNewDeviceEvent {
    private final UserModel user;
    private final PasswordResetTokenModel token;
    private final IpAddress ipAddress;
    private final UserAgent userAgent;
}
