package com.auth.app.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    boolean existsByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    // ✅ Check if valid token still active
    @Query("""
        SELECT COUNT(r) > 0
        FROM RefreshToken r
        WHERE r.user.id = :userId
        AND r.tokenHash = :tokenHash
        AND r.revoked = FALSE
        AND r.expiresAt > CURRENT_TIMESTAMP
    """)
    boolean existsValidToken(
            @Param("userId") UUID userId,
            @Param("tokenHash") String tokenHash
    );

    // ✅ Revoke by token hash
    @Modifying
    @Transactional
    @Query("""
        UPDATE RefreshToken r
        SET r.revoked = TRUE
        WHERE r.tokenHash = :tokenHash
    """)
    void revokeByTokenHash(@Param("tokenHash") String tokenHash);

    // ✅ Revoke all tokens for user except current device
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE RefreshToken r
        SET r.revoked = TRUE
        WHERE r.user.id = :userId
        AND r.deviceId <> :deviceId
        AND r.revoked = FALSE
    """)
    void revokeAllExceptCurrentDevice(
            @Param("userId") UUID userId,
            @Param("deviceId") UUID deviceId
    );

    // ✅ Revoke all tokens for a specific device
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE RefreshToken r
        SET r.revoked = TRUE
        WHERE r.user.id = :userId
        AND r.deviceId = :deviceId
        AND r.revoked = FALSE
    """)
    int revokeByDeviceId(
            @Param("userId") UUID userId,
            @Param("deviceId") UUID deviceId
    );

    // ✅ Revoke all tokens for a user
    @Modifying
    @Transactional
    @Query("""
        UPDATE RefreshToken r
        SET r.revoked = TRUE
        WHERE r.user.id = :userId
        AND r.revoked = FALSE
    """)
    void revokeAllByUserId(@Param("userId") UUID userId);
}
