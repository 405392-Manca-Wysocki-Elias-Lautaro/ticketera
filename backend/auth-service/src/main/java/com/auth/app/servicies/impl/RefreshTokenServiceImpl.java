package com.auth.app.servicies.impl;

import java.time.OffsetDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.app.entity.RefreshToken;
import com.auth.app.repositories.RefreshTokenRepository;

@Service
public class RefreshTokenServiceImpl {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RefreshToken create(Long userId, String rawToken, String userAgent, String ipAddress) {
        RefreshToken refreshEntity = new RefreshToken();
        refreshEntity.setUserId(userId);
        refreshEntity.setTokenHash(passwordEncoder.encode(rawToken)); // nunca en claro
        refreshEntity.setExpiresAt(OffsetDateTime.now().plusDays(7));
        refreshEntity.setUserAgent(userAgent);
        refreshEntity.setIpAddress(ipAddress);

        return refreshTokenRepository.save(refreshEntity);
    }

    public void revokeAllByUser(Long userId) {
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    public boolean validate(Long userId, String rawToken) {
        return refreshTokenRepository.findByUserId(userId).stream()
            .anyMatch(stored -> passwordEncoder.matches(rawToken, stored.getTokenHash()) && !stored.isRevoked());
    }
}
