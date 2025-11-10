package com.auth.app.domain.model;

import java.time.OffsetDateTime;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private String password;
    private RoleModel role;
    private boolean mfaEnabled;
    private boolean isActive;
    private OffsetDateTime lastLoginAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
