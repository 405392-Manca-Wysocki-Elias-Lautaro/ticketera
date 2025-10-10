package com.auth.app.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.auth.app.dto.RoleDTO;
import com.auth.app.repositories.RoleRepository;
import com.auth.app.services.RoleService;
import com.auth.app.entity.RoleCode;
import com.auth.app.entity.Role;
import com.auth.app.exception.exceptions.EntityNotFoundException;

@Service
public class RoleServiceImpl implements RoleService {

    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    public RoleServiceImpl(
            RoleRepository roleRepository,
            ModelMapper modelMapper
    ) {
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RoleDTO findByCode(RoleCode code) {
        return roleRepository.findByCode(code)
            .map(role -> modelMapper.map(role, RoleDTO.class))
            .orElseThrow(() -> new EntityNotFoundException(Role.class, "code", code));
    }
}
