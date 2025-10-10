package com.auth.app.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.auth.app.entity.Role;
import com.auth.app.entity.RoleCode;

public interface  RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(RoleCode code);
}
