package com.auth.app.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.response.UserResponse;
import com.auth.app.entity.Role;
import com.auth.app.entity.RoleCode;
import com.auth.app.entity.User;
import com.auth.app.exception.exceptions.EmailAlreadyExistsException;
import com.auth.app.exception.exceptions.EntityNotFoundException;
import com.auth.app.repositories.UserRepository;
import com.auth.app.security.PasswordValidator;
import com.auth.app.services.UserService;
import com.auth.app.services.RoleDomainService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleDomainService roleDomainService;

    public UserServiceImpl(
        UserRepository userRepository,
        ModelMapper modelMapper,
        PasswordEncoder passwordEncoder,
        RoleDomainService roleService
    ) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleDomainService = roleService;
    }

    @Override
    public User create(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // validar contraseña
        PasswordValidator.validate(request.getPassword());

        User user = modelMapper.map(request, User.class);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmailVerified(false);
        user.setActive(true);

        user.setRole(modelMapper.map(roleDomainService.findByCode(RoleCode.CUSTOMER), Role.class));

        return userRepository.save(user);
    }

    @Override
    public UserResponse getById(Long id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserResponse.class))
                .orElseThrow(() -> new EntityNotFoundException(User.class, "id", id));
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(User.class, "id", id);
        }
        userRepository.deleteById(id);
    }
}
