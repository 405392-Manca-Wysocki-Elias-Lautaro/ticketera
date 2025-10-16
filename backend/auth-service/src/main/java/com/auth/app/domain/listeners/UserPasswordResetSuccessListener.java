package com.auth.app.domain.listeners;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.auth.app.domain.events.UserPasswordResetSuccessEvent;
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
public class UserPasswordResetSuccessListener {

    private final NotificationSender notificationSender;
    @Value("${support.email}")
    String supportEmail;

    @Async
    @EventListener
    public void handleUserPasswordResetSuccess(UserPasswordResetSuccessEvent event) {

        UserModel user = event.getUser();
        try {

            String loginLink = event.getFrontendUrl() + "/login";

            notificationSender.send(NotificationDTO.builder()
                    .to(user.getEmail())
                    .channel(NotificationChannel.EMAIL)
                    .type(NotificationType.PASSWORD_RESET_SUCCESS)
                    .variables(Map.of(
                            "firstName", user.getFirstName(),
                            "link", loginLink,
                            "supportEmail", supportEmail,
                            "ipAddress", event.getIpAddress(),
                            "userAgent", event.getUserAgent(),
                            "timestamp", event.getTimestamp()
                    ))
                    .build()
            );

            log.info("Password reset success email sent to {}", user.getEmail());

        } catch (Exception e) {
            log.error("Error sending password reset success email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

}
