package com.auth.app.services.domain;

import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.model.UserModel;

public interface AuditLogService {
    void logAction(UserModel user, LogAction action, String ipAddress, String userAgent);
}
