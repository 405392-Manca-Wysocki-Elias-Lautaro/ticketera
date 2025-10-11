package com.auth.app.domain.model;

import java.time.OffsetDateTime;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttemptModel  {
    private UUID id;
    private UUID userId;
    private String email;
    private String ipAddress;
    private boolean success;
    private OffsetDateTime attemptedAt;
}

