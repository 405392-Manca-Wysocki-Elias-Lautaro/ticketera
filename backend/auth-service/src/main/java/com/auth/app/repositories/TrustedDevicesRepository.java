package com.auth.app.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.auth.app.domain.entity.TrustedDevice;

public interface TrustedDevicesRepository extends JpaRepository<TrustedDevice, UUID> {

    Optional<TrustedDevice> findByUserIdAndUserAgentAndIpAddress(UUID userId, String userAgent, String ipAddress);

    boolean existsByUserIdAndUserAgentAndIpAddress(UUID userId, String userAgent, String ipAddress);

    @Query("SELECT d FROM TrustedDevice d WHERE d.user.id = :userId AND d.ipAddress = :ip AND d.userAgent = :ua AND d.trusted = true")
    Optional<TrustedDevice> findByUserAndDeviceFingerprint(
            @Param("userId") UUID userId,
            @Param("ip") String ipAddress,
            @Param("ua") String userAgent
    );

}
