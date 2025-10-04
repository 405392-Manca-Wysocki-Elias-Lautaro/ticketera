package com.auth.app.servicies;

import java.util.List;

import com.auth.app.dto.request.RegisterRequest;
import com.auth.app.dto.response.UserResponse;
import com.auth.app.entity.User;

public interface UserService {
    User create(RegisterRequest request);
    UserResponse getById(Long id);
    List<UserResponse> getAll();
    void delete(Long id);
}
