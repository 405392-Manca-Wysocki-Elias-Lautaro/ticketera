package com.auth.app.domain.listeners;

import com.auth.app.domain.events.UserLoginFromNewDeviceEvent;
import com.auth.app.domain.enums.LogAction;
import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;
import com.auth.app.notification.entity.NotificationChannel;
import com.auth.app.notification.entity.NotificationType;
import com.auth.app.services.domain.AuditLogService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Map;

import com.auth.app.domain.model.UserModel;

@Slf4j
@Component
@RequiredArgsConstructor
@Async
public class UserLoginFromNewDeviceListener {

    private final NotificationSender notificationSender;
    private final AuditLogService auditLogService;

    @EventListener
    public void handle(UserLoginFromNewDeviceEvent event) {
        UserModel user = event.getUser();

        notificationSender.send(NotificationDTO.builder()
                .channel(NotificationChannel.EMAIL.name())
                .type(NotificationType.LOGIN_ALERT.name())
                .to(user.getEmail())
                .variables(Map.of(
                        "firstName", user.getFirstName(),
                        "ipAddress", event.getIpAddress(),
                        "userAgent", event.getUserAgent(),
                        "timestamp", OffsetDateTime.now().toString(),
                        "link", event.getResetPasswordLink()
                ))
                .build()
        );

        auditLogService.logAction(user, LogAction.LOGIN_ALERT_EMAIL_SENT,
                event.getIpAddress(), event.getUserAgent());

        log.info("[NOTIFICATION] Sent new device login alert to {}", user.getEmail());
    }
}
