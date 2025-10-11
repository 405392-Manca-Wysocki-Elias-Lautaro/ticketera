package com.auth.app.services.domain;

import java.util.List;
import java.util.UUID;

import com.auth.app.domain.model.UserModel;

public interface UserService {
    UserModel create(UserModel request);
    UserModel update(UUID id, UserModel request);
    UserModel findByEmail(String email);
    UserModel findById(UUID id);
    List<UserModel> getAll();
    void delete(UUID id);
}
