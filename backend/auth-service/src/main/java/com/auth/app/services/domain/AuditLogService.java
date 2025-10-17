package com.auth.app.services.domain;

import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.model.UserModel;
import com.auth.app.domain.valueObjects.IpAddress;
import com.auth.app.domain.valueObjects.UserAgent;

public interface AuditLogService {
    void logAction(UserModel user, LogAction action, IpAddress ipAddress, UserAgent userAgent);
}
