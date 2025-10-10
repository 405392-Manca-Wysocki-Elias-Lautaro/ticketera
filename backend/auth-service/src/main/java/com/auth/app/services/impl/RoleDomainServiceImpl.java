package com.auth.app.services.impl;

import org.springframework.stereotype.Service;

import com.auth.app.entity.Role;
import com.auth.app.entity.RoleCode;
import com.auth.app.exception.exceptions.EntityNotFoundException;
import com.auth.app.repositories.RoleRepository;
import com.auth.app.services.RoleDomainService;

@Service
public class RoleDomainServiceImpl implements RoleDomainService {

    private final RoleRepository roleRepository;

    public RoleDomainServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByCode(RoleCode code) {
        return roleRepository.findByCode(code)
            .orElseThrow(() -> new EntityNotFoundException(Role.class, "code", code.name()));
    }
}

