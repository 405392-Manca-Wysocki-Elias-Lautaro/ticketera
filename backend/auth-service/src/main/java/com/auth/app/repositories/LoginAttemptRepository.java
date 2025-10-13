package com.auth.app.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.auth.app.domain.entity.LoginAttempt;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, UUID> {

@Query(value = """
    SELECT COUNT(*) 
    FROM login_attempts l
    JOIN users u ON u.id = l.user_id
    WHERE u.email = :email
    AND l.success = false
    AND l.attempted_at > NOW() - (:minutes * INTERVAL '1 minute')
    """, nativeQuery = true)
    Optional<Integer> countRecentFailedAttempts(@Param("email") String email, @Param("minutes") long minutes);

}
