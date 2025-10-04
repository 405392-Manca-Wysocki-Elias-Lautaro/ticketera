package com.notification.app.strategies.email;

import com.notification.app.dto.GenericNotificationDTO;

public interface EmailNotificationStrategy {
    String getType();
    void send(GenericNotificationDTO dto);
}
