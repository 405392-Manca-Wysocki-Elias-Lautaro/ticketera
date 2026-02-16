package com.notification.app.entity;

import java.util.EnumSet;
import java.util.Set;

public enum NotificationType {
    // 🔐 Autenticación
    EMAIL_VERIFICATION(EnumSet.of(NotificationChannel.EMAIL)),
    PASSWORD_RESET_REQUEST(EnumSet.of(NotificationChannel.EMAIL)),
    PASSWORD_RESET_SUCCESS(EnumSet.of(NotificationChannel.EMAIL)),
    USER_WELCOME(EnumSet.of(NotificationChannel.EMAIL)),
    LOGIN_ALERT(EnumSet.of(NotificationChannel.EMAIL)),

    // 🎟️ Eventos y tickets
    EVENT_REMINDER(EnumSet.of(NotificationChannel.EMAIL));
    // ORDER_STATUS_UPDATE(EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP)),
    // ORDER_CONFIRMED(EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.WHATSAPP, NotificationChannel.TELEGRAM)),

    // // 📢 Marketing
    // PROMOTIONAL_MESSAGE(EnumSet.of(NotificationChannel.EMAIL, NotificationChannel.TELEGRAM));

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
