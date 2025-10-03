package com.auth.app.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.auth.app.entity.ApiKey;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKey(String key);
}
