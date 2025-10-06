package com.auth.app.repositories;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.entity.EmailVerificationToken;


public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :now OR t.used = true")
    int deleteExpiredTokens(OffsetDateTime now);
}
