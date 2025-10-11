package com.auth.app.services.domain;

import com.auth.app.domain.model.UserModel;

public interface EmailVerificatonService {
    String generateToken(UserModel user);
    UserModel verifyToken(String rawToken);
}
