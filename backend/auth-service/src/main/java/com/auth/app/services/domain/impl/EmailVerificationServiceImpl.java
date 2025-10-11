package com.auth.app.services.domain.impl;

import java.time.OffsetDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.EmailVerificationToken;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.model.EmailVerificationTokenModel;
import com.auth.app.domain.model.UserModel;
import com.auth.app.repositories.EmailVerificationRepository;
import com.auth.app.security.TokenUtils;
import com.auth.app.services.domain.EmailVerificatonService;
import com.auth.app.services.domain.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificatonService {

    private final EmailVerificationRepository emailVerificationTokenRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public String generateToken(UserModel user) {
        String rawToken = TokenUtils.generateToken();
        String hashToken = TokenUtils.hashToken(rawToken);

        EmailVerificationTokenModel token = EmailVerificationTokenModel.builder()
                .user(user)
                .tokenHash(hashToken)
                .expiresAt(OffsetDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        emailVerificationTokenRepository.save(modelMapper.map(token, EmailVerificationToken.class));
        return rawToken; // este se envía por correo

    }

    @Override
    @Transactional
    public UserModel verifyToken(String rawToken) {
        log.info("rawToken: {}", rawToken);

        String hash = TokenUtils.hashToken(rawToken);
        log.info("hashToken: {}", hash);

        EmailVerificationToken token = emailVerificationTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new RuntimeException("Invalid or unknown token"));

        if (token.getUsed()) {
            throw new RuntimeException("Token already used");
        }

        if (token.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        token.setUsed(true);
        emailVerificationTokenRepository.save(token);

        User user = token.getUser();
        user.setEmailVerified(true);
        user.setActive(true);
        UserModel updated = userService.update(user.getId(), modelMapper.map(user, UserModel.class));
        
        emailVerificationTokenRepository.deleteAllByUserIdExcept(user.getId(), token.getId());

        return updated;
    }

}
