package com.notification.app.strategies.email;

import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.entity.NotificationType;

public interface EmailNotificationStrategy {
    NotificationType getType();
    void send(GenericNotificationDTO dto);
}
