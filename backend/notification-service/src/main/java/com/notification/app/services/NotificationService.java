package com.notification.app.services;

import com.notification.app.dto.GenericNotificationDTO;

public interface NotificationService {
    void send(GenericNotificationDTO dto);
}
