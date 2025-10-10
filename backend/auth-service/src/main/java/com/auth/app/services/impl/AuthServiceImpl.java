package com.auth.app.services.impl;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.response.AuthResponse;
import com.auth.app.dto.response.UserResponse;
import com.auth.app.entity.User;
import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;
import com.auth.app.notification.entity.NotificationChannel;
import com.auth.app.notification.entity.NotificationType;
import com.auth.app.security.TokenProvider;
import com.auth.app.services.AuthService;
import com.auth.app.services.RefreshTokenService;
import com.auth.app.services.UserService;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;
    private final ModelMapper modelMapper;
    private final NotificationSender notificationSender;

    public AuthServiceImpl(
            UserService userService,
            RefreshTokenService refreshTokenService,
            TokenProvider tokenProvider,
            ModelMapper modelMapper,
            NotificationSender notificationSender
    ) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.tokenProvider = tokenProvider;
        this.modelMapper = modelMapper;
        this.notificationSender = notificationSender;
    }

    @Override
    public AuthResponse register(RegisterRequest request, String userAgent, String ipAddress) {

        User saved = userService.create(request);

        // tokens
        String accessToken = tokenProvider.generateAccessToken(saved);
        String refreshToken = tokenProvider.generateRefreshToken(saved);

        // delegar creación del refresh token
        refreshTokenService.create(saved.getId(), refreshToken, userAgent, ipAddress);

        notificationSender.send(
                NotificationDTO.builder()
                        .channel(NotificationChannel.EMAIL.toString())
                        .type(NotificationType.EMAIL_VERIFICATION.toString())
                        .to(saved.getEmail())
                        .variables(Map.of(
                                "firstName", saved.getFirstName(),
                                "lastName", saved.getLastName(),
                                "link", "https://auth.ticketera.com/verify?token=" + "123456789" //TODO: Reemplazar por token de verificacion
                        ))
                        .build()
        );

        // respuesta
        UserResponse userResponse = modelMapper.map(saved, UserResponse.class);
        return new AuthResponse(accessToken, refreshToken, userResponse);
    }
}
