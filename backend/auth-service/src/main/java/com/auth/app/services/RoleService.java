package com.auth.app.services;

import com.auth.app.dto.RoleDTO;
import com.auth.app.entity.RoleCode;

public interface RoleService {
    public RoleDTO findByCode(RoleCode name);
}   
