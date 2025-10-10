package com.auth.app.notification.entity;

import java.util.EnumSet;
import java.util.Set;

public enum NotificationType {
    // 🔐 Autenticación
    EMAIL_VERIFICATION(EnumSet.of(NotificationChannel.EMAIL)),
    PASSWORD_RESET(EnumSet.of(NotificationChannel.EMAIL)),
    USER_WELCOME(EnumSet.of(NotificationChannel.EMAIL));

    private final Set<NotificationChannel> validChannels;

    NotificationType(Set<NotificationChannel> validChannels) {
        this.validChannels = validChannels;
    }

    public Set<NotificationChannel> getValidChannels() {
        return validChannels;
    }

    public boolean supports(NotificationChannel channel) {
        return validChannels.contains(channel);
    }
}
