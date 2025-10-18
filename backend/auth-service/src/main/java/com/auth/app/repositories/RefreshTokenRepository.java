package com.auth.app.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.RefreshToken;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("""
        SELECT COUNT(r) > 0 FROM RefreshToken r
            WHERE r.user.id = :userId
            AND r.tokenHash = :tokenHash
            AND r.revoked = FALSE
            AND r.expiresAt > CURRENT_TIMESTAMP
            AND r.ipAddress = :ipAddress
            AND r.userAgent = :userAgent
        """)
    boolean existsValidToken(
            @Param("userId") UUID userId,
            @Param("tokenHash") String tokenHash,
            @Param("ipAddress") IpAddress ipAddress,
            @Param("userAgent") UserAgent userAgent
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE RefreshToken r
            SET r.revoked = TRUE,
            WHERE r.tokenHash = :tokenHash
        """)
    void revokeByTokenHash(@Param("tokenHash") String tokenHash);

    @Modifying
    @Transactional
    @Query("""
            UPDATE RefreshToken r
            SET r.revoked = TRUE,
            WHERE r.user.id = :userId
            AND (r.ipAddress <> :ipAddress OR r.userAgent <> :userAgent)
            AND r.revoked = FALSE
        """)
    void revokeAllExceptCurrent(
            @Param("userId") UUID userId,
            @Param("ipAddress") IpAddress ipAddress,
            @Param("userAgent") UserAgent userAgent
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE RefreshToken r
            SET r.revoked = TRUE,
            WHERE r.user.id = :userId
            AND r.ipAddress = :ipAddress
            AND r.userAgent = :userAgent
            AND r.revoked = FALSE
        """)
    void revokeByUserAndDevice(
            @Param("userId") UUID userId,
            @Param("ipAddress") IpAddress ipAddress,
            @Param("userAgent") UserAgent userAgent
    );

    @Modifying
    @Transactional
    @Query("""
            UPDATE RefreshToken r
            SET r.revoked = TRUE,
            WHERE r.user.id = :userId
            AND r.revoked = FALSE
        """)
    void revokeAllByUserId(@Param("userId") UUID userId);
}
