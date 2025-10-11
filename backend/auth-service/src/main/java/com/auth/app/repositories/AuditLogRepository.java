package com.auth.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

import com.auth.app.domain.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    
}
