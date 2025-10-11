package com.auth.app.domain.model;

import java.time.OffsetDateTime;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenModel {
    private UUID id;
    private UUID userId;
    private String tokenHash;
    private boolean used;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;
}

