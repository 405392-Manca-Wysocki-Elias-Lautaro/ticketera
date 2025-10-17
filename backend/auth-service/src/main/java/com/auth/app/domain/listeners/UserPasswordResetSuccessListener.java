package com.auth.app.domain.listeners;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.events.UserPasswordResetSuccessEvent;
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
public class UserPasswordResetSuccessListener {

    private final NotificationSender notificationSender;
    private final AuditLogService auditLogService;
    private final FrontendUrlBuilder frontendUrlBuilder;
    @Value("${support.email}")
    String supportEmail;

    @Async
    @EventListener
    public void handleUserPasswordResetSuccess(UserPasswordResetSuccessEvent event) {

        UserModel user = event.getUser();
        String link = frontendUrlBuilder.buildLoginUrl();

        auditLogService.logAction(user, LogAction.PASSWORD_RESET_SUCCESS_EMAIL_SENT,
                event.getIpAddress(), event.getUserAgent());

        try {
            notificationSender.send(NotificationDTO.builder()
                    .to(user.getEmail())
                    .channel(NotificationChannel.EMAIL)
                    .type(NotificationType.PASSWORD_RESET_SUCCESS)
                    .variables(Map.of(
                            "firstName", user.getFirstName(),
                            "link", link,
                            "supportEmail", supportEmail,
                            "ipAddress", event.getIpAddress(),
                            "userAgent", event.getUserAgent(),
                            "timestamp", event.getTimestamp()
                    ))
                    .build()
            );

        } catch (Exception e) {
            log.error(
                    "[NOTIFICATION] Error sending password reset success email to {}: {}",
                    user.getEmail(), e.getMessage()
            );

            auditLogService.logAction(user, LogAction.PASSWORD_RESET_SUCCESS_EMAIL_ERROR,
                    event.getIpAddress(), event.getUserAgent());

        }
    }

}
