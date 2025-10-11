package com.auth.app.services.domain;

import java.util.UUID;

public interface AuditLogService {
    void logAction(UUID userId, String action, String description, String ip, String userAgent);
}
