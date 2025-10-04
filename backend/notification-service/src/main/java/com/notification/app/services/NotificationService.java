package com.notification.app.services;

import com.notification.app.dto.GenericNotificationDTO;

public interface NotificationService {
    public void send(GenericNotificationDTO dto);
}
