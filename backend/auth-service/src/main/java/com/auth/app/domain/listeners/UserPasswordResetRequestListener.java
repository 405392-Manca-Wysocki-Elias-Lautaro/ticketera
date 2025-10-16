package com.auth.app.domain.listeners;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.auth.app.domain.events.UserPasswordResetRequestEvent;
import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.model.UserModel;
import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;
import com.auth.app.notification.entity.NotificationChannel;
import com.auth.app.notification.entity.NotificationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPasswordResetRequestListener {

    private final NotificationSender notificationSender;

    @Async
    @EventListener
    public void handleUserPasswordResetRequested(UserPasswordResetRequestEvent event) {
        PasswordResetTokenModel token = event.getToken();
        UserModel user = token.getUser();

        try {
            String resetLink = event.getFrontendUrl() + "/reset-password?token=" + token.getToken();

            notificationSender.send(NotificationDTO.builder()
                    .to(user.getEmail())
                    .channel(NotificationChannel.EMAIL)
                    .type(NotificationType.PASSWORD_RESET_REQUEST)
                    .variables(Map.of(
                            "firstName", user.getFirstName(),
                            "link", resetLink,
                            "ipAddress", event.getIpAddress(),
                            "userAgent", event.getUserAgent(),
                            "timestamp", event.getTimestamp(),
                            "expirationMinutes", event.getExpirationMinutes()
                    ))
                    .build()
            );

            log.info("Password reset request email sent to {}", user.getEmail());

        } catch (Exception e) {
            log.error("Error sending password reset request email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
