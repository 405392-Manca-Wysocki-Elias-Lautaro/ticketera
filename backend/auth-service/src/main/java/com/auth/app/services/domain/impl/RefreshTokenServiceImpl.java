package com.auth.app.services.domain.impl;

import java.time.OffsetDateTime;

import org.springframework.transaction.annotation.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.auth.app.domain.entity.RefreshToken;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.model.UserModel;
import com.auth.app.exception.exceptions.InvalidRefreshTokenException;
import com.auth.app.repositories.RefreshTokenRepository;
import com.auth.app.services.domain.AuditLogService;
import com.auth.app.services.domain.RefreshTokenService;
import com.auth.app.utils.TokenUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final ModelMapper modelMapper;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public String create(UserModel user, String ipAddress, String userAgent, boolean remembered) {

        String rawToken = TokenUtils.generateToken();
        String hashedToken = TokenUtils.hashToken(rawToken);

        OffsetDateTime expiration = remembered
                ? OffsetDateTime.now().plusDays(60)
                : OffsetDateTime.now().plusDays(1);

        RefreshToken refreshEntity = new RefreshToken();
        refreshEntity.setUser(modelMapper.map(user, User.class));
        refreshEntity.setTokenHash(hashedToken);
        refreshEntity.setExpiresAt(expiration);
        refreshEntity.setRemembered(remembered);
        refreshEntity.setUserAgent(userAgent);
        refreshEntity.setIpAddress(ipAddress);

        refreshTokenRepository.save(refreshEntity);

        return rawToken;
    }

    @Override
    @Transactional
    public void revokeAllByUser(UserModel user) {
        refreshTokenRepository.revokeAllByUserId(user.getId());
        log.info("Revoked all refresh tokens for user {}", user.getEmail());
        auditLogService.logAction(user, LogAction.TOKEN_REVOKED, null, null);

    }

    @Override
    public String rotateToken(UserModel user, String ipAddress, String userAgent, boolean remembered) {
        revokeAllByUser(user);

        String rawToken = create(user, userAgent, ipAddress, remembered);

        auditLogService.logAction(user, LogAction.REFRESH_TOKEN_ROTATED, ipAddress, userAgent);

        return rawToken;
    }

    @Override
    public boolean validate(UserModel user, String rawToken, String ipAddress, String userAgent) {
        return refreshTokenRepository.existsValidToken(user.getId(), TokenUtils.hashToken(rawToken), ipAddress, userAgent);
    }

    @Override
    @Transactional
    public String rotateFromRefresh(UserModel user, String currentRawToken, String ipAddress, String userAgent, boolean remembered) {
        if (!validate(user, currentRawToken, ipAddress, userAgent)) {
            auditLogService.logAction(user, LogAction.REFRESH_TOKEN_INVALID, ipAddress, userAgent);
            throw new InvalidRefreshTokenException();
        }

        refreshTokenRepository.revokeByTokenHash(TokenUtils.hashToken(currentRawToken));
        auditLogService.logAction(user, LogAction.REFRESH_TOKEN_USED, ipAddress, userAgent);

        String newToken = create(user, ipAddress, userAgent, remembered);
        auditLogService.logAction(user, LogAction.REFRESH_TOKEN_ROTATED, ipAddress, userAgent);
        return newToken;
    }

}
