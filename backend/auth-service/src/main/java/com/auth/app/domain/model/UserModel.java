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
public class UserModel {
    private UUID id;
    private UUID organizationId;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private String password;
    private String passwordHash;
    private RoleModel role;
    private boolean mfaEnabled;
    private boolean isActive;
    private OffsetDateTime lastLoginAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
