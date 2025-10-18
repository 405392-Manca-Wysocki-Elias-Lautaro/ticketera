package com.auth.app.repositories;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.TrustedDevice;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

public interface TrustedDevicesRepository extends JpaRepository<TrustedDevice, UUID> {

    Optional<TrustedDevice> findByUserIdAndIpAddressAndUserAgent(UUID userId, IpAddress ipAddress, UserAgent userAgent);

    boolean existsByUserIdAndIpAddressAndUserAgent(UUID userId, IpAddress ipAddress, UserAgent userAgent);

    @Transactional
    @Query("SELECT d FROM TrustedDevice d WHERE d.user.id = :userId AND d.ipAddress = :ip AND d.userAgent = :ua AND d.trusted = true")
    Optional<TrustedDevice> findByUserAndDeviceFingerprint(
            @Param("userId") UUID userId,
            @Param("ip") IpAddress ipAddress,
            @Param("ua") UserAgent userAgent
    );

    @Modifying
    @Transactional
    @Query("""
    UPDATE TrustedDevice td
        SET td.trusted = FALSE,
            td.revokedAt = :revokedAt
        WHERE td.user.id = :userId
        AND (td.ipAddress <> :ipAddress OR td.userAgent <> :userAgent)
        AND td.trusted = TRUE
    """)
    int revokeAllExceptCurrent(
            @Param("userId") UUID userId,
            @Param("ipAddress") IpAddress ipAddress,
            @Param("userAgent") UserAgent userAgent,
            @Param("revokedAt") OffsetDateTime revokedAt
    );

}
