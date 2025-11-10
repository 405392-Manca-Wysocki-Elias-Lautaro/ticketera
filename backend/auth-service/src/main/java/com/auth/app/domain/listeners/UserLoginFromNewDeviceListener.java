package com.auth.app.domain.listeners;

import java.time.OffsetDateTime;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.events.UserLoginFromNewDeviceEvent;
import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.model.UserModel;
import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;
import com.auth.app.notification.entity.NotificationChannel;
import com.auth.app.notification.entity.NotificationType;
import com.auth.app.services.domain.AuditLogService;
import com.auth.app.utils.FrontendUrlBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Async
public class UserLoginFromNewDeviceListener {

    private final NotificationSender notificationSender;
    private final AuditLogService auditLogService;
    private final FrontendUrlBuilder frontendUrlBuilder;

    @EventListener
    public void handle(UserLoginFromNewDeviceEvent event) {
        UserModel user = event.getUser();
        PasswordResetTokenModel token = event.getToken();

        String link = frontendUrlBuilder.buildResetPasswordUrl(token.getToken());

        auditLogService.logAction(user, LogAction.LOGIN_ALERT_EMAIL_SENT,
                event.getIpAddress(), event.getUserAgent());

        try {
            notificationSender.send(NotificationDTO.builder()
                    .channel(NotificationChannel.EMAIL)
                    .type(NotificationType.LOGIN_ALERT)
                    .to(user.getEmail())
                    .variables(Map.of(
                            "firstName", user.getFirstName(),
                            "ipAddress", event.getIpAddress().toString(),
                            "userAgent", event.getUserAgent().toString(),
                            "timestamp", OffsetDateTime.now().toString(),
                            "link", link
                    ))
                    .build()
            );
        } catch (Exception e) {
            log.info(
                    "[NOTIFICATION] Sent new device login alert to {}: ",
                    user.getEmail(), e.getMessage()
            );

            auditLogService.logAction(user, LogAction.LOGIN_ALERT_EMAIL_ERROR,
                    event.getIpAddress(), event.getUserAgent());
        }
    }
}
