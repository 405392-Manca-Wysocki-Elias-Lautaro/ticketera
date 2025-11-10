package com.auth.app.domain.model;

import java.time.OffsetDateTime;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationTokenModel {
    private UUID id;
    private UserModel user;
    private String tokenHash;
    private boolean used;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiresAt;
}

