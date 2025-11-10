package com.auth.app.domain.listeners;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.events.UserPasswordResetRequestEvent;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPasswordResetRequestListener {

    private final NotificationSender notificationSender;
    private final AuditLogService auditLogService;
    private final FrontendUrlBuilder frontendUrlBuilder;

    @Async
    @EventListener
    public void handleUserPasswordResetRequested(UserPasswordResetRequestEvent event) {
        PasswordResetTokenModel token = event.getToken();
        UserModel user = token.getUser();
        String link = frontendUrlBuilder.buildResetPasswordUrl(token.getToken());

        auditLogService.logAction(user, LogAction.PASSWORD_RESET_REQUEST_EMAIL_SENT,
                event.getIpAddress(), event.getUserAgent());

        try {

            notificationSender.send(NotificationDTO.builder()
                    .to(user.getEmail())
                    .channel(NotificationChannel.EMAIL)
                    .type(NotificationType.PASSWORD_RESET_REQUEST)
                    .variables(Map.of(
                            "firstName", user.getFirstName(),
                            "link", link,
                            "ipAddress", event.getIpAddress().toString(),
                            "userAgent", event.getUserAgent().toString(),
                            "timestamp", event.getTimestamp(),
                            "expirationMinutes", event.getExpirationMinutes()
                    ))
                    .build()
            );

        } catch (Exception e) {
            log.error(
                    "[NOTIFICATION] Error sending password reset request email to {}: {}",
                    user.getEmail(), e.getMessage()
            );

            auditLogService.logAction(user, LogAction.PASSWORD_RESET_REQUEST_EMAIL_ERROR,
                    event.getIpAddress(), event.getUserAgent());

        }
    }
}
