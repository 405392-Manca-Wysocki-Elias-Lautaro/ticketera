package com.auth.app.services.domain;

import java.util.UUID;

import com.auth.app.domain.enums.RoleCode;
import com.auth.app.domain.model.RoleModel;

public interface RoleService {
    RoleModel findById(UUID id);
    RoleModel findByCode(RoleCode name);
}   
