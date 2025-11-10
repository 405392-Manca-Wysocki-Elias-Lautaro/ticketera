package com.notification.app.strategies;


import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.entity.NotificationChannel;

public interface NotificationChannelStrategy {
    NotificationChannel getChannelType();
    void send(GenericNotificationDTO dto);
}
