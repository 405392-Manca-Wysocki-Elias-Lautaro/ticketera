package com.auth.app.domain.events;

import com.auth.app.domain.model.UserModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserVerifiedEvent {
    private final UserModel user;
    private final String frontendUrl;
}
