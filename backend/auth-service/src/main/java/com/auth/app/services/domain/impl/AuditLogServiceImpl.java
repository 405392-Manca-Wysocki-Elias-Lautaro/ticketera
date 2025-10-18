package com.auth.app.services.domain.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth.app.domain.entity.AuditLog;
import com.auth.app.domain.entity.User;
import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;
import com.auth.app.repositories.AuditLogRepository;
import com.auth.app.services.domain.AuditLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void logAction(UserModel user, LogAction action, IpAddress ipAddress, UserAgent userAgent) {
        AuditLog log = AuditLog.builder()
                .user(modelMapper.map(user, User.class))
                .actionCode(action.getCode())
                .action(action.name())
                .description(action.getDescription())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(log);
    }
}

