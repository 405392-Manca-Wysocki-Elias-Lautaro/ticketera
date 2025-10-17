package com.auth.app.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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
        AND r.revoked = false
        AND r.expiresAt > CURRENT_TIMESTAMP
        AND r.ipAddress = :ipAddress
        AND r.userAgent = :userAgent
    """)
    boolean existsValidToken(UUID userId, String tokenHash, IpAddress ipAddress, UserAgent userAgent);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.tokenHash = :tokenHash")
    void revokeByTokenHash(String tokenHash);

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId")
    void revokeAllByUserId(UUID userId);
}
