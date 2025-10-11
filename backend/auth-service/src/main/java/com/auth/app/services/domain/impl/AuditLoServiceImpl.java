package com.auth.app.services.domain.impl;

import org.springframework.stereotype.Service;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.AuditLog;
import com.auth.app.repositories.AuditLogRepository;
import com.auth.app.services.domain.AuditLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLoServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void logAction(UUID userId, String action, String description, String ip, String userAgent) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .description(description)
                .ipAddress(ip)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(log);
    }
}

