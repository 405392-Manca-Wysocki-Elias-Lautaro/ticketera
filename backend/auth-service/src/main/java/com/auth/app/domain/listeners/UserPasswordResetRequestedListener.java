package com.auth.app.domain.listeners;

import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.auth.app.domain.events.UserPasswordResetRequestedEvent;
import com.auth.app.domain.model.PasswordResetTokenModel;
import com.auth.app.domain.model.UserModel;
import com.auth.app.notification.entity.NotificationType;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserPasswordResetRequestedListener {

    private final NotificationSender notificationSender;

    @Async
    @EventListener
    public void handleUserPasswordResetRequested(UserPasswordResetRequestedEvent event) {
        PasswordResetTokenModel token = event.getToken();
        UserModel user = token.getUser();

        try {
            String resetLink = event.getFrontendUrl() + "/reset-password?token=" + token.getToken();

            notificationSender.send(NotificationDTO.builder()
                    .to(user.getEmail())
                    .type(NotificationType.PASSWORD_RESET)
                    .variables(Map.of(
                            "name", user.getFirstName(),
                            "link", resetLink
                    ))
                    .build()
            );

            log.info("Password reset email sent to {}", user.getEmail());

        } catch (Exception e) {
            log.error("Error sending password reset email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
