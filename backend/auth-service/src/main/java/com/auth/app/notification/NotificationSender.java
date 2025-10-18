package com.auth.app.notification;

import com.auth.app.notification.dto.NotificationDTO;

public interface NotificationSender {
    void send(NotificationDTO notification);
}
