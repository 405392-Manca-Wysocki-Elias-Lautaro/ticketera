package com.auth.app.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.auth.app.domain.entity.LoginAttempt;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {

    @Query("""
        SELECT COUNT(l) 
        FROM LoginAttempt l 
        WHERE l.user.email = :email
        AND l.success = false
        AND l.attemptedAt > CURRENT_TIMESTAMP - (:minutes * INTERVAL '1 minute')
    """)
    Optional<Integer> countRecentFailedAttempts(String email, long minutes);

}
