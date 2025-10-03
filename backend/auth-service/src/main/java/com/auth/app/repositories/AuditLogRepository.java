package com.auth.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.auth.app.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
}
