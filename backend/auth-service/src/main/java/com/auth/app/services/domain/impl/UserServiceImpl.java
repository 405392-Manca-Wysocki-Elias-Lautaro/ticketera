package com.auth.app.services.domain.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.Role;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.enums.RoleCode;
import com.auth.app.domain.model.UserModel;
import com.auth.app.exception.exceptions.EmailAlreadyExistsException;
import com.auth.app.exception.exceptions.EntityNotFoundException;
import com.auth.app.repositories.UserRepository;
import com.auth.app.security.PasswordValidator;
import com.auth.app.services.domain.RoleService;
import com.auth.app.services.domain.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    @Transactional
    public UserModel create(UserModel userModel) {
        if (userRepository.existsByEmail(userModel.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        PasswordValidator.validate(userModel.getPassword());

        User user = modelMapper.map(userModel, User.class);
        user.setPasswordHash(passwordEncoder.encode(userModel.getPassword()));
        user.setEmailVerified(false);
        user.setActive(false);

        user.setRole(modelMapper.map(roleService.findByCode(RoleCode.CUSTOMER), Role.class));

        return modelMapper.map(userRepository.save(user), UserModel.class);
    }

    @Override
    @Transactional
    public UserModel update(UUID id, UserModel userModel) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class, "id", id));

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.map(userModel, user);

        if (userModel.getRole().getId() != null) {
            Role role = modelMapper.map(roleService.findById(userModel.getRole().getId()), Role.class);
            user.setRole(role);
        }

        return modelMapper.map(userRepository.save(user), UserModel.class);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(User.class, "id", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<UserModel> getAll() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserModel findById(UUID id) {
        return userRepository.findById(id)
                .map(user -> modelMapper.map(user, UserModel.class))
                .orElseThrow(() -> new EntityNotFoundException(User.class, "id", id));
    }

    @Override
    public UserModel findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> modelMapper.map(user, UserModel.class))
                .orElseThrow(() -> new EntityNotFoundException(User.class, "email", email));
    }

    @Override
    public Optional<UserModel> findOptionalByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> modelMapper.map(user, UserModel.class));
    }

}
