package com.auth.app.services.domain;

import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

public interface LoginAttemptService {
    void registerFailedAttempt(UserModel user, IpAddress ip, UserAgent userAgent);
    void registerSuccessfulAttempt(UserModel user, IpAddress ip, UserAgent userAgent);
    boolean isBlocked(String email);
}
