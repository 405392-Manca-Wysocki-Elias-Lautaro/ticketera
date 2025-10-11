package com.auth.app.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private UUID roleId;
    private Boolean mfaEnabled;
    private Boolean isActive;
}
