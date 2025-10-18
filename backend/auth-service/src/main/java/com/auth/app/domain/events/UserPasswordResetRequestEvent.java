package com.auth.app.domain.events;


import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserPasswordResetRequestEvent {

    private final PasswordResetTokenModel token;
    private final IpAddress ipAddress;
    private final UserAgent userAgent;
    private final String timestamp;
    private final Integer expirationMinutes;

}
