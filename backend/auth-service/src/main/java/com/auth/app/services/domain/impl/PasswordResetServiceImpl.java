package com.auth.app.services.domain.impl;

import java.time.OffsetDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth.app.domain.entity.PasswordResetToken;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.model.UserModel;
import com.auth.app.exception.exceptions.InvalidOrUnknownTokenException;
import com.auth.app.repositories.PasswordResetTokenRepository;
import com.auth.app.services.domain.PasswordResetService;
import com.auth.app.utils.TokenUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    @Value("${security.password-reset.expiration-minutes}")
    private int expirationMinutes;
    private final PasswordResetTokenRepository passwordResetRepository;
    private final ModelMapper modelMapper;

    @Override
    public PasswordResetTokenModel createToken(UserModel user) {

        String token = TokenUtils.generateToken();

        PasswordResetToken reset = PasswordResetToken.builder()
                .user(modelMapper.map(user, User.class))
                .tokenHash(TokenUtils.hashToken(token))
                .expiresAt(OffsetDateTime.now().plusMinutes(expirationMinutes))
                .used(false)
                .build();

        PasswordResetTokenModel passwordResetTokenModel = modelMapper.map(
                passwordResetRepository.save(reset),
                PasswordResetTokenModel.class
        );

        passwordResetTokenModel.setToken(token);
        return passwordResetTokenModel;
    }

    @Override
    public PasswordResetToken findByTokenHash(String tokenHash) {
        return passwordResetRepository.findByTokenHash(tokenHash)
                .map(passwordResetToken -> modelMapper.map(passwordResetToken, PasswordResetToken.class))
                .orElseThrow(() -> new InvalidOrUnknownTokenException());
    }

    @Override
    public void update(PasswordResetToken token) {
        passwordResetRepository.save(token);
    }

}
