package com.auth.app.services.impl;

import java.time.OffsetDateTime;

import org.springframework.stereotype.Service;

import com.auth.app.entity.RefreshToken;
import com.auth.app.repositories.RefreshTokenRepository;
import com.auth.app.security.TokenUtils;
import com.auth.app.services.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken create(Long userId, String rawToken, String userAgent, String ipAddress) {
        String hashedToken = TokenUtils.hashToken(rawToken);

        RefreshToken refreshEntity = new RefreshToken();
        refreshEntity.setUserId(userId);
        refreshEntity.setTokenHash(hashedToken);
        refreshEntity.setExpiresAt(OffsetDateTime.now().plusDays(7));
        refreshEntity.setUserAgent(userAgent);
        refreshEntity.setIpAddress(ipAddress);

        return refreshTokenRepository.save(refreshEntity);
    }

    @Override
    public void revokeAllByUser(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Override
    public boolean validate(Long userId, String rawToken) {
        String hashedToken = TokenUtils.hashToken(rawToken);
        return refreshTokenRepository.findByUserId(userId).stream()
                .anyMatch(stored -> hashedToken.equals(stored.getTokenHash()) && !stored.isRevoked());
    }
}
