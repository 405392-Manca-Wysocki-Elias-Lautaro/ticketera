package com.auth.app.notification.dto;

import java.io.Serializable;
import java.util.Map;

import com.auth.app.notification.entity.NotificationChannel;
import com.auth.app.notification.entity.NotificationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDTO implements Serializable {
    private NotificationChannel channel;
    private NotificationType type;
    private String to;
    private Map<String, Object> variables;
}
