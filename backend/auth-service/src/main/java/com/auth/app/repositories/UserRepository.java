package com.auth.app.repositories;

import com.auth.app.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import com.auth.app.domain.enums.RoleCode;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    List<User> findByOrganizationIdAndRole_Code(UUID organizationId, RoleCode roleCode);
}