package com.auth.app.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.auth.app.domain.entity.ApiKey;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByTokenHash(String key);
}
