package com.auth.app.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.app.domain.entity.TrustedDevice;

public interface TrustedDevicesRepository extends JpaRepository<TrustedDevice, UUID> {
    Optional<TrustedDevice> findByUserIdAndUserAgentAndIpAddress(UUID userId, String userAgent, String ipAddress);

    boolean existsByUserIdAndUserAgentAndIpAddress(UUID userId, String userAgent, String ipAddress);
}
