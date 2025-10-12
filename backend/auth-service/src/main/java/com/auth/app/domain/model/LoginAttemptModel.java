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
public class LoginAttemptModel  {
    private UUID id;
    private UserModel user;
    private String email;
    private String ipAddress;
    private boolean success;
    private OffsetDateTime attemptedAt;
}

