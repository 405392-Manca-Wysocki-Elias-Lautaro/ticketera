package com.auth.app.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

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
    private boolean success;
    private IpAddress ipAddress;
    private UserAgent userAgent;
    private OffsetDateTime attemptedAt;
}

