package com.auth.app.repositories;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import com.auth.app.domain.entity.TrustedDevice;

public interface TrustedDevicesRepository extends JpaRepository<TrustedDevice, UUID> {

    Optional<TrustedDevice> findByUserIdAndDeviceId(UUID userId, UUID deviceId);

    boolean existsByUserIdAndDeviceId(UUID userId, UUID deviceId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE TrustedDevice td
        SET td.trusted = FALSE,
            td.revokedAt = :revokedAt
        WHERE td.user.id = :userId
        AND td.deviceId <> :currentDeviceId
        AND td.trusted = TRUE
    """)
    int revokeAllExceptCurrent(
            @Param("userId") UUID userId,
            @Param("currentDeviceId") UUID currentDeviceId,
            @Param("revokedAt") OffsetDateTime revokedAt
    );
}
