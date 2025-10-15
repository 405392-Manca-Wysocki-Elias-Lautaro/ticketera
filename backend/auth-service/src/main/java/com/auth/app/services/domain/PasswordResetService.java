package com.auth.app.services.domain;


import com.auth.app.domain.entity.PasswordResetToken;
import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.model.UserModel;

public interface PasswordResetService {

    PasswordResetTokenModel createToken(UserModel user);

    PasswordResetToken findByTokenHash(String tokenHash);

    void update(PasswordResetToken token);

}
