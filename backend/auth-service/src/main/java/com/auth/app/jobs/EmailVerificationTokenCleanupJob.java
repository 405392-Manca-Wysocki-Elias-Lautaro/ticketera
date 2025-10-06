package com.auth.app.jobs;

import java.time.OffsetDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.auth.app.repositories.EmailVerificationTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailVerificationTokenCleanupJob {

    private final EmailVerificationTokenRepository tokenRepository;

    // Ejecuta todos los días a las 2 AM
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanExpiredTokens() {
        int deleted = tokenRepository.deleteExpiredTokens(OffsetDateTime.now());
        log.info("🧹 Limpieza de tokens de verificación: {} registros eliminados", deleted);
    }
}

