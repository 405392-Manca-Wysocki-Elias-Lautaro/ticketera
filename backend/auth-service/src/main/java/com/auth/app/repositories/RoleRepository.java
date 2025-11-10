package com.auth.app.repositories;

import com.auth.app.domain.entity.Role;
import com.auth.app.domain.enums.RoleCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByCode(RoleCode code);
}