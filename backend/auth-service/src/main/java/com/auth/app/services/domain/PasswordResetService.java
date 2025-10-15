package com.auth.app.services.domain;

import java.util.Optional;

import com.auth.app.domain.entity.PasswordResetToken;
import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.model.UserModel;

public interface PasswordResetService {

    PasswordResetTokenModel createToken(UserModel user);

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    void update(PasswordResetToken token);

}
