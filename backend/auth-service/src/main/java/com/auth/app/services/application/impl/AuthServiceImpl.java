package com.auth.app.services.application.impl;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.app.domain.entity.PasswordResetToken;
import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.events.UserLoginFromNewDeviceEvent;
import com.auth.app.domain.events.UserPasswordResetRequestEvent;
import com.auth.app.domain.events.UserPasswordResetSuccessEvent;
import com.auth.app.domain.events.UserVerificationEmailEvent;
import com.auth.app.domain.events.UserWelcomeEvent;
import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;
import com.auth.app.dto.request.ChangePasswordRequest;
import com.auth.app.dto.request.ForgotPasswordRequest;
import com.auth.app.dto.request.LoginRequest;
import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.request.ResetPasswordRequest;
import com.auth.app.dto.response.AuthResponse;
import com.auth.app.dto.response.UserResponse;
import com.auth.app.exception.exceptions.AccountNotVerifiedException;
import com.auth.app.exception.exceptions.InvalidCredentialsException;
import com.auth.app.exception.exceptions.InvalidRefreshTokenException;
import com.auth.app.exception.exceptions.SamePasswordException;
import com.auth.app.exception.exceptions.TokenAlreadyUsedException;
import com.auth.app.exception.exceptions.TokenExpiredException;
import com.auth.app.exception.exceptions.TooManyAttemptsException;
import com.auth.app.security.TokenProvider;
import com.auth.app.services.application.AuthService;
import com.auth.app.services.domain.AuditLogService;
import com.auth.app.services.domain.EmailVerificatonService;
import com.auth.app.services.domain.LoginAttemptService;
import com.auth.app.services.domain.PasswordResetService;
import com.auth.app.services.domain.RefreshTokenService;
import com.auth.app.services.domain.TrustedDevicesService;
import com.auth.app.services.domain.UserService;
import com.auth.app.utils.EnvironmentUtils;
import com.auth.app.utils.TokenUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;
    private final EmailVerificatonService emailVerificationService;
    private final ModelMapper modelMapper;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final TrustedDevicesService trustedDevicesService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordResetService passwordResetService;

    @Value("${security.password-reset.expiration-minutes}")
    private Integer resetPasswordTokenExpirationMinutes;

    @Override
    public AuthResponse register(RegisterRequest request, IpAddress ipAddress, UserAgent userAgent) {

        UserModel saved = userService.create(modelMapper.map(request, UserModel.class));

        // tokens
        String accessToken = tokenProvider.generateAccessToken(saved);
        String refreshToken = refreshTokenService.create(saved, ipAddress, userAgent, false);

        String verifyEmailToken = emailVerificationService.generateToken(saved);

        if (EnvironmentUtils.isDev()) {
            log.info("🗝️ Verify Email Token: {}", verifyEmailToken);
        }

        applicationEventPublisher.publishEvent(
                new UserVerificationEmailEvent(saved, verifyEmailToken, ipAddress, userAgent)
        );

        auditLogService.logAction(saved, LogAction.USER_REGISTERED,
                ipAddress, userAgent);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(tokenProvider.getAccessTokenExpirationMs())
                .user(modelMapper.map(saved, UserResponse.class))
                .build();
    }

    @Override
    public void resendVerificationEmail(String email, IpAddress ipAddress, UserAgent userAgent) {

        UserModel user = userService.findByEmail(email);
        String verifyEmailToken = emailVerificationService.generateNewVerificationToken(email);

        if (EnvironmentUtils.isDev()) {
            log.info("🗝️ Resend Verify Email Token: {}", verifyEmailToken);
        }

        applicationEventPublisher.publishEvent(
                new UserVerificationEmailEvent(user, verifyEmailToken, ipAddress, userAgent)
        );

        auditLogService.logAction(user, LogAction.USER_VERIFICATION_EMAIL_RESENT, ipAddress, userAgent);
    }

    @Override
    public void verifyEmail(String rawToken, IpAddress ipAddress, UserAgent userAgent) {
        try {
            UserModel verifiedUser = emailVerificationService.verifyToken(rawToken);

            log.info("verified user: {}", verifiedUser);

            applicationEventPublisher.publishEvent(new UserWelcomeEvent(verifiedUser, ipAddress, userAgent));
            auditLogService.logAction(verifiedUser, LogAction.USER_EMAIL_VERIFIED, ipAddress, userAgent);

        } catch (TokenExpiredException | InvalidRefreshTokenException e) {
            auditLogService.logAction(null, LogAction.USER_EMAIL_VERIFICATION_FAILED, ipAddress, userAgent);
            throw e;
        }

    }

    @Override
    public AuthResponse login(LoginRequest request, IpAddress ipAddress, UserAgent userAgent) {

        UserModel user = userService.findByEmail(request.getEmail());
        if (user == null) {
            auditLogService.logAction(null, LogAction.USER_LOGIN_FAILED, ipAddress, userAgent);
            throw new InvalidCredentialsException();
        }

        if (!user.isEmailVerified() || !user.isActive()) {
            log.warn("[LOGIN_FAIL] Unverified account login attempt email={} IP={} UA={}",
                    request.getEmail(), ipAddress, userAgent);
            auditLogService.logAction(user, LogAction.USER_LOGIN_BLOCKED, ipAddress, userAgent);
            throw new AccountNotVerifiedException();
        }

        if (loginAttemptService.isBlocked(request.getEmail())) {
            auditLogService.logAction(null, LogAction.LOGIN_RATE_LIMITED, ipAddress, userAgent);
            throw new TooManyAttemptsException();
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("[LOGIN_FAIL] Invalid credentials for email={} IP={} UA={}",
                    request.getEmail(), ipAddress, userAgent);

            loginAttemptService.registerFailedAttempt(user, ipAddress, userAgent);

            auditLogService.logAction(user, LogAction.USER_LOGIN_FAILED, ipAddress, userAgent);

            throw new InvalidCredentialsException();
        }

        loginAttemptService.registerSuccessfulAttempt(user, ipAddress, userAgent);

        String accessToken = tokenProvider.generateAccessToken(user);
        String refreshToken = refreshTokenService.rotateToken(user, ipAddress, userAgent, request.isRemembered());

        auditLogService.logAction(user, LogAction.USER_LOGIN, ipAddress, userAgent);

        boolean isNewDevice = trustedDevicesService.isNewDevice(user, ipAddress, userAgent);

        if (isNewDevice) {
            PasswordResetTokenModel token = passwordResetService.createToken(user);
            applicationEventPublisher.publishEvent(
                    new UserLoginFromNewDeviceEvent(user, token, ipAddress, userAgent)
            );
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(tokenProvider.getAccessTokenExpirationMs())
                .user(modelMapper.map(user, UserResponse.class))
                .build();
    }

    @Override
    public AuthResponse refresh(String rawRefreshToken, IpAddress ipAddress, UserAgent userAgent) {

        UserModel user = tokenProvider.extractUserFromRefreshToken(rawRefreshToken);

        boolean isValid = refreshTokenService.validate(user, rawRefreshToken, ipAddress, userAgent);
        if (!isValid) {
            auditLogService.logAction(user, LogAction.REFRESH_TOKEN_INVALID, ipAddress, userAgent);
            throw new InvalidRefreshTokenException();
        }

        String newRefresh = refreshTokenService.rotateFromRefresh(user, rawRefreshToken, ipAddress, userAgent, false);

        String newAccess = tokenProvider.generateAccessToken(user);

        auditLogService.logAction(user, LogAction.TOKEN_REFRESHED, ipAddress, userAgent);

        return AuthResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .expiresIn(tokenProvider.getAccessTokenExpirationMs())
                .user(modelMapper.map(user, UserResponse.class))
                .build();
    }

    @Override
    public void logout(String authorizationHeader, IpAddress ipAddress, UserAgent userAgent) {

        UserModel user = tokenProvider.extractUserFromAuthorizationHeader(authorizationHeader);

        refreshTokenService.revokeCurrentToken(user, ipAddress, userAgent);

        trustedDevicesService.unregisterCurrentDevice(user, ipAddress, userAgent);

        auditLogService.logAction(user, LogAction.USER_LOGOUT, ipAddress, userAgent);
    }

    @Override
    public void logoutFromOtherDevices(String authorizationHeader, IpAddress ipAddress, UserAgent userAgent) {
        UserModel user = tokenProvider.extractUserFromAuthorizationHeader(authorizationHeader);

        refreshTokenService.revokeAllExceptCurrent(user, ipAddress, userAgent);

        trustedDevicesService.unregisterAllExceptCurrent(user, ipAddress, userAgent);

        auditLogService.logAction(user, LogAction.USER_LOGOUT_OTHER_DEVICES, ipAddress, userAgent);
    }

    @Override
    public void changePassword(String authorizationHeader, ChangePasswordRequest request, IpAddress ipAddress, UserAgent userAgent) {
        UserModel user = tokenProvider.extractUserFromAuthorizationHeader(authorizationHeader);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            auditLogService.logAction(user, LogAction.USER_PASSWORD_CHANGE_FAILED, ipAddress, userAgent);
            throw new InvalidCredentialsException();
        }

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            auditLogService.logAction(user, LogAction.USER_PASSWORD_CHANGE_FAILED, ipAddress, userAgent);
            throw new SamePasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.update(user.getId(), user);
        refreshTokenService.revokeAllByUser(user, ipAddress, userAgent);

        auditLogService.logAction(user, LogAction.USER_PASSWORD_CHANGED, ipAddress, userAgent);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request, IpAddress ipAddress, UserAgent userAgent) {
        Optional<UserModel> userOpt = userService.findOptionalByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            PasswordResetTokenModel token = passwordResetService.createToken(user);

            String timestamp = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(Instant.now());

            applicationEventPublisher.publishEvent(
                    new UserPasswordResetRequestEvent(
                            token,
                            ipAddress,
                            userAgent,
                            timestamp,
                            resetPasswordTokenExpirationMinutes
                    )
            );

            auditLogService.logAction(user, LogAction.USER_PASSWORD_RESET_REQUEST, ipAddress, userAgent);
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request, IpAddress ipAddress, UserAgent userAgent) {
        String rawToken = request.getToken();
        String hashed = TokenUtils.hashToken(rawToken);

        PasswordResetToken resetToken = passwordResetService.findByTokenHash(hashed);

        if (resetToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            auditLogService.logAction(
                    modelMapper.map(resetToken.getUser(), UserModel.class),
                    LogAction.USER_PASSWORD_RESET_FAILED,
                    ipAddress, userAgent
            );
            throw new TokenExpiredException();
        }
        if (resetToken.isUsed()) {
            auditLogService.logAction(
                    modelMapper.map(resetToken.getUser(), UserModel.class),
                    LogAction.USER_PASSWORD_RESET_FAILED,
                    ipAddress, userAgent
            );
            throw new TokenAlreadyUsedException();
        }

        UserModel user = modelMapper.map(resetToken.getUser(), UserModel.class);

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new SamePasswordException();
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.update(user.getId(), user);

        resetToken.setUsed(true);
        passwordResetService.update(resetToken);

        refreshTokenService.revokeAllByUser(user, ipAddress, userAgent);

        String timestamp = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now());

        applicationEventPublisher.publishEvent(
                new UserPasswordResetSuccessEvent(
                        user,
                        ipAddress,
                        userAgent,
                        timestamp
                )
        );

        auditLogService.logAction(user, LogAction.USER_PASSWORD_RESET_COMPLETED, null, null);

        log.info("Password reset successfully for user {}", user.getEmail());
    }

    @Override
    public UserResponse getCurrentUser(String authorizationHeader, IpAddress ipAddress, UserAgent userAgent) {

        UserModel user = tokenProvider.extractUserFromAuthorizationHeader(authorizationHeader);

        auditLogService.logAction(user, LogAction.USER_PROFILE_FETCHED, ipAddress, userAgent);

        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public boolean validateAccessToken(String token) {
        return tokenProvider.validateAccessToken(token);
    }

}
