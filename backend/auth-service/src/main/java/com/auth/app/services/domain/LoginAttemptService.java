package com.auth.app.services.domain;

import com.auth.app.domain.model.UserModel;

public interface LoginAttemptService {
    void registerFailedAttempt(UserModel user, String ip, String userAgent);
    void registerSuccessfulAttempt(UserModel user, String ip, String userAgent);
    boolean isBlocked(String email);
}
