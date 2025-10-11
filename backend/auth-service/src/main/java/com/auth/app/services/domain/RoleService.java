package com.auth.app.services.domain;

import com.auth.app.domain.entity.RoleCode;
import com.auth.app.domain.model.RoleModel;
import java.util.UUID;

public interface RoleService {
    RoleModel findById(UUID id);
    RoleModel findByCode(RoleCode name);
}   
