package com.auth.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthModel {
    private String accessToken;
    private RefreshTokenModel refreshToken;
    private Long expiresIn;
    private UserModel user;
    private String tokenType;
    private java.util.UUID deviceId;

}

