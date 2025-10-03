package com.auth.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.auth.app.entity.UserRole;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByUserId(Long userId);
    Optional<UserRole> findByRoleId(Long roleId);
}
