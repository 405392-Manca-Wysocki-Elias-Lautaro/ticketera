package com.auth.app.services;

import com.auth.app.entity.Role;
import com.auth.app.entity.RoleCode;

public interface RoleDomainService {
    public Role findByCode(RoleCode code);
}
