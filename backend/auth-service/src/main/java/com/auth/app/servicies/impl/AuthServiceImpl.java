package com.auth.app.servicies.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.response.AuthResponse;
import com.auth.app.dto.response.UserResponse;
import com.auth.app.entity.User;
import com.auth.app.security.TokenProvider;
import com.auth.app.servicies.RefreshTokenService;
import com.auth.app.servicies.UserService;

@Service
public class AuthServiceImpl {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;
    private final ModelMapper modelMapper;

    public AuthServiceImpl(
            UserService userService,
            RefreshTokenService refreshTokenService,
            TokenProvider tokenProvider,
            ModelMapper modelMapper
    ) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.tokenProvider = tokenProvider;
        this.modelMapper = modelMapper;
    }

    public AuthResponse register(RegisterRequest request, String userAgent, String ipAddress) {

        User saved = userService.create(request);

        // tokens
        String accessToken = tokenProvider.generateAccessToken(saved);
        String refreshToken = tokenProvider.generateRefreshToken(saved);

        // delegar creación del refresh token
        refreshTokenService.create(saved.getId(), refreshToken, userAgent, ipAddress);

        // respuesta
        UserResponse userResponse = modelMapper.map(saved, UserResponse.class);
        return new AuthResponse(accessToken, refreshToken, userResponse);
    }
}
