package com.auth.app.services.application.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.app.domain.entity.PasswordResetToken;
import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.events.UserLoginFromNewDeviceEvent;
import com.auth.app.domain.events.UserPasswordResetRequestedEvent;
import com.auth.app.domain.events.UserRegisteredEvent;
import com.auth.app.domain.events.UserVerifiedEvent;
import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.model.UserModel;
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

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public AuthResponse register(RegisterRequest request, String ipAddress, String userAgent) {

        UserModel saved = userService.create(modelMapper.map(request, UserModel.class));

        // tokens
        String accessToken = tokenProvider.generateAccessToken(saved);
        String refreshToken = refreshTokenService.create(saved, ipAddress, userAgent, false);

        String verifyEmailToken = emailVerificationService.generateToken(saved);

        if (EnvironmentUtils.isDev()) {
            log.info("🗝️ Verify Email Token: {}", verifyEmailToken);
        }

        applicationEventPublisher.publishEvent(
                new UserRegisteredEvent(saved, frontendUrl + "/verify?token=" + verifyEmailToken)
        );

        UserResponse userResponse = modelMapper.map(saved, UserResponse.class);
        return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    @Override
    public void resendVerificationEmail(String email) {

        UserModel user = userService.findByEmail(email);

        String verifyEmailToken = emailVerificationService.generateNewVerificationToken(email);

        if (EnvironmentUtils.isDev()) {
            log.info("🗝️ Resend Verify Email Token: {}", verifyEmailToken);
        }

        applicationEventPublisher.publishEvent(
                new UserRegisteredEvent(user, frontendUrl + "/verify?token=" + verifyEmailToken)
        );
    }

    @Override
    public void verifyEmail(String rawToken, String ipAddress, String userAgent) {
        UserModel verifiedUser = emailVerificationService.verifyToken(rawToken);

        applicationEventPublisher.publishEvent(new UserVerifiedEvent(verifiedUser, frontendUrl));

        auditLogService.logAction(verifiedUser, LogAction.EMAIL_VERIFIED, ipAddress, userAgent);
    }

    @Override
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {

        UserModel user = userService.findByEmail(request.getEmail());
        if (user == null) {
            auditLogService.logAction(null, LogAction.USER_LOGIN_FAILED, ipAddress, userAgent);
            throw new InvalidCredentialsException();
        }

        if (!user.isEmailVerified() || !user.isActive()) {
            log.warn("[LOGIN_FAIL] Unverified account login attempt email={} IP={} UA={}",
                    request.getEmail(), ipAddress, userAgent);

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
            //TODO: Enviar reset password token
            applicationEventPublisher.publishEvent(
                    new UserLoginFromNewDeviceEvent(user, ipAddress, userAgent, frontendUrl + "/verify?token=" + "12345")
            );
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(modelMapper.map(user, UserResponse.class))
                .build();
    }

    @Override
    public AuthResponse refresh(String rawRefreshToken, String ipAddress, String userAgent) {

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
                .user(modelMapper.map(user, UserResponse.class))
                .build();
    }

    @Override
    public void logout(String authorizationHeader, String ipAddress, String userAgent) {

        UserModel user = tokenProvider.extractUserFromAuthorizationHeader(authorizationHeader);

        refreshTokenService.revokeAllByUser(user);

        auditLogService.logAction(user, LogAction.USER_LOGOUT, ipAddress, userAgent);

        trustedDevicesService.unregisterCurrentDevice(user, ipAddress, userAgent);
    }

    @Override
    public void changePassword(String authorizationHeader, ChangePasswordRequest request, String ipAddress, String userAgent) {
        UserModel user = tokenProvider.extractUserFromAuthorizationHeader(authorizationHeader);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            auditLogService.logAction(user, LogAction.USER_PASSWORD_CHANGE_FAILED, ipAddress, userAgent);
            throw new InvalidCredentialsException();
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.update(user.getId(), user);
        refreshTokenService.revokeAllByUser(user);

        auditLogService.logAction(user, LogAction.USER_PASSWORD_CHANGED, ipAddress, userAgent);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request, String ipAddress, String userAgent) {
        Optional<UserModel> userOpt = userService.findOptionalByEmail(request.getEmail());

        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            PasswordResetTokenModel token = passwordResetService.createToken(user);

            applicationEventPublisher.publishEvent(
                    new UserPasswordResetRequestedEvent(token, frontendUrl)
            );

            auditLogService.logAction(user, LogAction.USER_PASSWORD_RESET_REQUEST, ipAddress, userAgent);
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request, String ipAddress, String userAgent) {
        String rawToken = request.getToken();
        String hashed = TokenUtils.hashToken(rawToken);

        PasswordResetToken resetToken = passwordResetService.findByTokenHash(hashed);

        if (resetToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new TokenExpiredException();
        }
        if (resetToken.isUsed()) {
            throw new TokenAlreadyUsedException();
        }

        // 3️⃣ Cambiar la contraseña
        UserModel user = modelMapper.map(resetToken.getUser(), UserModel.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.update(user.getId(), user);

        // 4️⃣ Marcar el token como usado
        resetToken.setUsed(true);
        passwordResetService.update(resetToken);

        // 5️⃣ Revocar refresh tokens (seguridad)
        refreshTokenService.revokeAllByUser(user);

        // 6️⃣ Auditoría
        auditLogService.logAction(user, LogAction.USER_PASSWORD_RESET_COMPLETED, null, null);

        log.info("Password reset successfully for user {}", user.getEmail());
    }

    @Override
    public UserResponse getCurrentUser(String authorizationHeader, String ipAddress, String userAgent) {

        UserModel user = tokenProvider.extractUserFromAuthorizationHeader(authorizationHeader);


        auditLogService.logAction(user, LogAction.USER_PROFILE_FETCHED, ipAddress, userAgent);


        return modelMapper.map(user, UserResponse.class);
    }

}
