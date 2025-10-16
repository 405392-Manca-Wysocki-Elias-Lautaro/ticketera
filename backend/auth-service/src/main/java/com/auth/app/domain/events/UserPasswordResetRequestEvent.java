package com.auth.app.domain.events;


import com.auth.app.domain.model.PasswordResetTokenModel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserPasswordResetRequestEvent {

    private final PasswordResetTokenModel token;
    private final String frontendUrl;
    private final String ipAddress;
    private final String userAgent;
    private final String timestamp;
    private final Integer expirationMinutes;

}
