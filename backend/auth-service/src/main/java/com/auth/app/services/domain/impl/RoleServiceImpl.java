package com.auth.app.services.domain.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

import com.auth.app.domain.entity.Role;
import com.auth.app.domain.enums.RoleCode;
import com.auth.app.domain.model.RoleModel;
import com.auth.app.repositories.RoleRepository;
import com.auth.app.services.domain.RoleService;

import lombok.RequiredArgsConstructor;

import com.auth.app.exception.exceptions.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    @Override
    public RoleModel findById(UUID id) {
        return roleRepository.findById(id)
            .map(role -> modelMapper.map(role, RoleModel.class))
            .orElseThrow(() -> new EntityNotFoundException(Role.class, "id", id));
    }

    @Override
    public RoleModel findByCode(RoleCode code) {
        return roleRepository.findByCode(code)
            .map(role -> modelMapper.map(role, RoleModel.class))
            .orElseThrow(() -> new EntityNotFoundException(Role.class, "code", code));

    }
}
