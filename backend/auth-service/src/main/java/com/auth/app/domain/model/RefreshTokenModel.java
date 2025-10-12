package com.auth.app.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenModel {
    private UUID id;
    private UserModel user;
    private String tokenHash;
    private String userAgent;
    private String ipAddress;
    private boolean remembered;
    private boolean revoked;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;
}
