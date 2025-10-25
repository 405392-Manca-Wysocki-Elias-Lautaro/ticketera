package com.auth.app.services.domain.impl;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.RefreshToken;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;
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

    // ✅ Crea un nuevo refresh token (con deviceId)
    @Override
    @Transactional
    public String create(UserModel user, UUID deviceId, IpAddress ipAddress, UserAgent userAgent, boolean remembered) {
        String rawToken = TokenUtils.generateToken();
        String hashedToken = TokenUtils.hashToken(rawToken);

        OffsetDateTime expiration = remembered
                ? OffsetDateTime.now().plusDays(60)
                : OffsetDateTime.now().plusDays(1);

        RefreshToken refreshEntity = RefreshToken.builder()
                .user(modelMapper.map(user, User.class))
                .deviceId(deviceId)
                .tokenHash(hashedToken)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .expiresAt(expiration)
                .remembered(remembered)
                .build();

        refreshTokenRepository.save(refreshEntity);

        log.info("[TOKEN_CREATE] user={} deviceId={} ip={} ua={}",
                user.getEmail(), deviceId, ipAddress.value(), userAgent.value());

        return rawToken;
    }

    // ✅ Revoca tokens por deviceId
    @Override
    @Transactional
    public void revokeByDevice(UserModel user, UUID deviceId, IpAddress ipAddress, UserAgent userAgent) {
        int affected = refreshTokenRepository.revokeByDeviceId(user.getId(), deviceId);

        if (affected == 0) {
            log.warn("[REVOKE_DEVICE] no tokens revoked for user={} device={}", user.getEmail(), deviceId);
        } else {
            log.info("[REVOKE_DEVICE] revoked {} tokens for user={} device={} ip={} ua={}",
                    affected, user.getEmail(), deviceId, ipAddress.value(), userAgent.value());
        }

        auditLogService.logAction(user, LogAction.TOKEN_REVOKED, ipAddress, userAgent);
    }

    // ✅ Revoca todos los tokens excepto el actual (basado en deviceId)
    @Override
    @Transactional
    public void revokeAllExceptCurrent(UserModel user, UUID currentDeviceId, IpAddress ipAddress, UserAgent userAgent) {
        refreshTokenRepository.revokeAllExceptCurrentDevice(user.getId(), currentDeviceId);
        log.info("[REVOKE_OTHERS] revoked all tokens except current for user={} device={} ip={} ua={}",
                user.getEmail(), currentDeviceId, ipAddress.value(), userAgent.value());
        auditLogService.logAction(user, LogAction.ALL_TOKENS_REVOKED_EXCEPT_CURRENT, ipAddress, userAgent);
    }

    // ✅ Revoca todos los tokens del usuario
    @Override
    @Transactional
    public void revokeAllByUser(UserModel user, IpAddress ipAddress, UserAgent userAgent) {
        refreshTokenRepository.revokeAllByUserId(user.getId());
        log.info("[REVOKE_ALL] revoked all refresh tokens for user={} ip={} ua={}",
                user.getEmail(), ipAddress.value(), userAgent.value());
        auditLogService.logAction(user, LogAction.ALL_TOKENS_REVOKED, ipAddress, userAgent);
    }

    // ✅ Rota el token actual (revoca y genera uno nuevo)
    @Override
    @Transactional
    public String rotateToken(UserModel user, UUID deviceId, IpAddress ipAddress, UserAgent userAgent, boolean remembered) {
        revokeByDevice(user, deviceId, ipAddress, userAgent);

        String newToken = create(user, deviceId, ipAddress, userAgent, remembered);

        auditLogService.logAction(user, LogAction.REFRESH_TOKEN_ROTATED, ipAddress, userAgent);

        return newToken;
    }

    // ✅ Valida un token de refresh
    @Override
    public boolean validate(UserModel user, String rawToken) {
        return refreshTokenRepository.existsValidToken(user.getId(), TokenUtils.hashToken(rawToken));
    }

    // ✅ Rota a partir de un refresh token existente
    @Override
    @Transactional
    public String rotateFromRefresh(UserModel user, String currentRawToken, UUID deviceId, IpAddress ipAddress, UserAgent userAgent, boolean remembered) {
        if (!validate(user, currentRawToken)) {
            auditLogService.logAction(user, LogAction.REFRESH_TOKEN_INVALID, ipAddress, userAgent);
            throw new InvalidRefreshTokenException();
        }

        refreshTokenRepository.revokeByTokenHash(TokenUtils.hashToken(currentRawToken));
        auditLogService.logAction(user, LogAction.REFRESH_TOKEN_USED, ipAddress, userAgent);

        String newToken = create(user, deviceId, ipAddress, userAgent, remembered);
        auditLogService.logAction(user, LogAction.REFRESH_TOKEN_ROTATED, ipAddress, userAgent);

        return newToken;
    }
}
