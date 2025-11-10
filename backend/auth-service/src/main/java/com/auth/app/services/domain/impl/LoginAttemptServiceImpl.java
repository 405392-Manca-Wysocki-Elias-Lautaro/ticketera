package com.auth.app.services.domain.impl;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.LoginAttempt;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;
import com.auth.app.repositories.LoginAttemptRepository;
import com.auth.app.services.domain.LoginAttemptService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 15; // si quisieras bloquear temporalmente

    private final LoginAttemptRepository loginAttemptRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void registerFailedAttempt(UserModel user, IpAddress ip, UserAgent userAgent) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUser(modelMapper.map(user, User.class));
        attempt.setIpAddress(ip);
        attempt.setUserAgent(userAgent);
        attempt.setSuccess(false);
        attempt.setAttemptedAt(OffsetDateTime.now());
        loginAttemptRepository.save(attempt);

        log.warn("[LOGIN_FAIL] Failed login attempt for user={} IP={} UA={}", 
                user.getEmail(), ip, userAgent);
    }

    @Override
    @Transactional
    public void registerSuccessfulAttempt(UserModel user, IpAddress ip, UserAgent userAgent) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setUser(modelMapper.map(user, User.class));
        attempt.setIpAddress(ip);
        attempt.setUserAgent(userAgent);
        attempt.setSuccess(true);
        attempt.setAttemptedAt(OffsetDateTime.now());
        loginAttemptRepository.save(attempt);

        log.info("[LOGIN_SUCCESS] Successful login for user={} IP={} UA={}", 
                user.getEmail(), ip, userAgent);
    }

    @Override
    public boolean isBlocked(String email) {
        Optional<Integer> failedCountOpt = loginAttemptRepository.countRecentFailedAttempts(email, LOCK_TIME_MINUTES);
        int failedCount = failedCountOpt.orElse(0);
        return failedCount >= MAX_FAILED_ATTEMPTS;
    }
}
