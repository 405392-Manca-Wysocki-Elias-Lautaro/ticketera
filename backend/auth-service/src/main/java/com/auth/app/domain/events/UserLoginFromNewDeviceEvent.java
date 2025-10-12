package com.auth.app.domain.events;

import com.auth.app.domain.model.UserModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserLoginFromNewDeviceEvent {
    private final UserModel user;
    private final String ipAddress;
    private final String userAgent;
}
