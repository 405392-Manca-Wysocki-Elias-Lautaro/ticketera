package com.auth.app.services.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.auth.app.domain.model.UserModel;

public interface UserService {
    UserModel create(UserModel request);
    UserModel update(UUID id, UserModel request);
    void delete(UUID id);
    List<UserModel> getAll();
    UserModel findById(UUID id);
    UserModel findByEmail(String email);
    Optional<UserModel> findOptionalByEmail(String email);
}
