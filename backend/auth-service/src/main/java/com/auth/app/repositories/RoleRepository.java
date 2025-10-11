package com.auth.app.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import com.auth.app.domain.entity.Role;
import com.auth.app.domain.entity.RoleCode;

public interface  RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByCode(RoleCode code);
}
