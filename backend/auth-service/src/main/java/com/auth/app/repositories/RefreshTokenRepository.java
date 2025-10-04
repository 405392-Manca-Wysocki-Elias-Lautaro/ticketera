package com.auth.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.app.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    void deleteByUserId(Long userId);
    boolean existsByUserId(Long userId);
    void revokeAllByUserId(Long userId);
    java.util.List<RefreshToken> findByUserId(Long userId);
}
