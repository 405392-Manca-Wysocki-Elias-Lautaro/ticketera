package com.auth.app.repositories;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.EmailVerificationToken;

public interface EmailVerificationRepository extends JpaRepository<EmailVerificationToken, UUID> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    @Transactional
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.user.id = :userId AND t.id <> :tokenId")
    int deleteAllByUserIdExcept(@Param("userId") UUID userId, @Param("tokenId") UUID tokenId);

    @Transactional
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :now OR t.used = true")
    int deleteExpiredTokens(OffsetDateTime now);
}
