package com.auth.app.domain.events;

import com.auth.app.domain.model.UserModel;

public class UserRegisteredEvent {
    private final UserModel user;
    private final String verificationLink;

    public UserRegisteredEvent(UserModel user, String verificationLink) {
        this.user = user;
        this.verificationLink = verificationLink;
    }

    public UserModel getUser() { return user; }
    public String getVerificationLink() { return verificationLink; }
}

