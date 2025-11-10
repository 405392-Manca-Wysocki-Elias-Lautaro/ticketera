package com.auth.app.domain.model;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenModel {
    private String token;
    private String tokenHash;
    private UserModel user;
    private boolean revoked;
    private OffsetDateTime expiresAt;
}
