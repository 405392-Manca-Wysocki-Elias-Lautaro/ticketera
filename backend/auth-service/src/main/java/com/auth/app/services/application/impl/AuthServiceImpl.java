package com.auth.app.services.application.impl;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth.app.domain.model.UserModel;
import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.response.AuthResponse;
import com.auth.app.dto.response.UserResponse;
import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;
import com.auth.app.notification.entity.NotificationChannel;
import com.auth.app.notification.entity.NotificationType;
import com.auth.app.security.TokenProvider;
import com.auth.app.security.TokenUtils;
import com.auth.app.services.application.AuthService;
import com.auth.app.services.domain.AuditLogService;
import com.auth.app.services.domain.EmailVerificatonService;
import com.auth.app.services.domain.RefreshTokenService;
import com.auth.app.services.domain.UserService;
import com.auth.app.utils.EnvironmentUtils;

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
    private final NotificationSender notificationSender;
    private final AuditLogService auditLogService;
    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public AuthResponse register(RegisterRequest request, String userAgent, String ipAddress) {

        UserModel saved = userService.create(modelMapper.map(request, UserModel.class));

        // tokens
        String accessToken = tokenProvider.generateAccessToken(saved);
        String refreshToken = TokenUtils.generateToken();

        // delegar creación del refresh token
        refreshTokenService.create(saved.getId(), refreshToken, userAgent, ipAddress, false);

        String verifyEmailToken = emailVerificationService.generateToken(saved);

        if (EnvironmentUtils.isDev()) {
            log.info("🗝️ Verify Email Token: {}", verifyEmailToken);
        }

        notificationSender.send(
                NotificationDTO.builder()
                        .channel(NotificationChannel.EMAIL.toString())
                        .type(NotificationType.EMAIL_VERIFICATION.toString())
                        .to(saved.getEmail())
                        .variables(Map.of(
                                "firstName", saved.getFirstName(),
                                "link", frontendUrl + "/verify?token=" + verifyEmailToken
                        ))
                        .build()
        );

        UserResponse userResponse = modelMapper.map(saved, UserResponse.class);
        return new AuthResponse(accessToken, refreshToken, userResponse);
    }

    @Override
    public void resendVerificationEmail(String email) {

        UserModel user = userService.findByEmail(email);

        String verifyEmailToken = emailVerificationService.resendVerificationEmail(email);

        if (EnvironmentUtils.isDev()) {
            log.info("🗝️ Resend Verify Email Token: {}", verifyEmailToken);
        }

        notificationSender.send(
                NotificationDTO.builder()
                        .channel(NotificationChannel.EMAIL.toString())
                        .type(NotificationType.EMAIL_VERIFICATION.toString())
                        .to(email)
                        .variables(Map.of(
                                "firstName", user.getFirstName(),
                                "link", frontendUrl + "/verify?token=" + verifyEmailToken
                        ))
                        .build()
        );
    }

    @Override
    public void verifyEmail(String rawToken, String ipAddress, String userAgent) {
        UserModel verifiedUser = emailVerificationService.verifyToken(rawToken);

        auditLogService.logAction(
                verifiedUser.getId(),
                "EMAIL_VERIFIED",
                "User verified their email successfully.",
                ipAddress,
                userAgent
        );

        notificationSender.send(
                NotificationDTO.builder()
                        .channel(NotificationChannel.EMAIL.toString())
                        .type(NotificationType.USER_WELCOME.toString())
                        .to(verifiedUser.getEmail())
                        .variables(Map.of(
                                "firstName", verifiedUser.getFirstName(),
                                "link", frontendUrl + "/login"
                        ))
                        .build()
        );
    }
    
}
