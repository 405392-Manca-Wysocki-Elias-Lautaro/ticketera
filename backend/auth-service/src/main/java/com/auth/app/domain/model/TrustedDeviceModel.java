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
public class TrustedDeviceModel {
    private UUID id;
    private UserModel user;
    private IpAddress ipAddress;
    private UserAgent userAgent;
    private String location;
    private boolean trusted;
    private OffsetDateTime revokedAt;
    private OffsetDateTime lastLogin;
    private OffsetDateTime createdAt;
}
