package com.auth.app.dto.response;

import java.util.UUID;

import lombok.Data;

@Data
public class RoleResponse {
    private UUID id;
    private String name;
    private String code;
}

