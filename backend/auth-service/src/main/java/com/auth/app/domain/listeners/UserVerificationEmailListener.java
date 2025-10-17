package com.auth.app.domain.listeners;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.auth.app.domain.enums.LogAction;
import com.auth.app.domain.events.UserVerificationEmailEvent;
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
public class UserVerificationEmailListener {

    private final NotificationSender notificationSender;
    private final AuditLogService auditLogService;
    private final FrontendUrlBuilder frontendUrlBuilder;

    @EventListener
    public void handleUserRegistered(UserVerificationEmailEvent event) {

        UserModel user = event.getUser();
        String link = frontendUrlBuilder.buildVerifyEmailUrl(event.getToken());

        auditLogService.logAction(user, LogAction.VERIFICATION_EMAIL_SENT,
                event.getIpAddress(), event.getUserAgent());

        try {
            notificationSender.send(NotificationDTO.builder()
                    .channel(NotificationChannel.EMAIL)
                    .type(NotificationType.EMAIL_VERIFICATION)
                    .to(user.getEmail())
                    .variables(Map.of(
                            "firstName", user.getFirstName(),
                            "link", link
                    ))
                    .build());

        } catch (Exception e) {
            log.error(
                    "[NOTIFICATION] Error sending user welcome email to {}: {}",
                    user.getEmail(), e.getMessage()
            );

            auditLogService.logAction(user, LogAction.VERIFICATION_EMAIL_ERROR,
                    event.getIpAddress(), event.getUserAgent());

        }
    }
}
