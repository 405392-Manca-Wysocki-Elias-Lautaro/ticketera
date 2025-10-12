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
public class PasswordResetTokenModel {
    private UUID id;
    private UserModel user;
    private String tokenHash;
    private boolean used;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;
}

