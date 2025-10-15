package com.auth.app.services.domain.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.auth.app.domain.entity.PasswordResetToken;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.model.UserModel;
import com.auth.app.repositories.PasswordResetTokenRepository;
import com.auth.app.services.domain.PasswordResetService;
import com.auth.app.utils.TokenUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository passwordResetRepository;
    private final ModelMapper modelMapper;

    @Override
    public PasswordResetTokenModel createToken(UserModel user) {

        String token = TokenUtils.generateToken();

        PasswordResetToken reset = PasswordResetToken.builder()
                .user(modelMapper.map(user, User.class))
                .tokenHash(TokenUtils.hashToken(token))
                .expiresAt(OffsetDateTime.now().plusMinutes(30))
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
    public Optional<PasswordResetToken> findByTokenHash(String tokenHash) {
        return passwordResetRepository.findByTokenHash(tokenHash);
    }

    @Override
    public void update(PasswordResetToken token) {
        passwordResetRepository.save(token);
    }

}
