package com.auth.app.services.domain.impl;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.auth.app.domain.entity.RefreshToken;
import com.auth.app.domain.model.RefreshTokenModel;
import com.auth.app.repositories.RefreshTokenRepository;
import com.auth.app.security.TokenUtils;
import com.auth.app.services.domain.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void create(UUID userId, String rawToken, String userAgent, String ipAddress, boolean remembered) {
        String hashedToken = TokenUtils.hashToken(rawToken);

        OffsetDateTime expiration = remembered
            ? OffsetDateTime.now().plusDays(60)
            : OffsetDateTime.now().plusDays(1);

        RefreshToken refreshEntity = new RefreshToken();
        refreshEntity.setUserId(userId);
        refreshEntity.setTokenHash(hashedToken);
        refreshEntity.setExpiresAt(expiration);
        refreshEntity.setRemembered(remembered);
        refreshEntity.setUserAgent(userAgent);
        refreshEntity.setIpAddress(ipAddress);

        modelMapper.map(refreshTokenRepository.save(refreshEntity), RefreshTokenModel.class);
    }

    @Override
    @Transactional
    public void revokeAllByUser(UUID userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Override
    public boolean validate(UUID userId, String rawToken) {
        String hashedToken = TokenUtils.hashToken(rawToken);
        return refreshTokenRepository.findByUserId(userId).stream()
                .anyMatch(stored -> hashedToken.equals(stored.getTokenHash()) && !stored.isRevoked());
    }
}
