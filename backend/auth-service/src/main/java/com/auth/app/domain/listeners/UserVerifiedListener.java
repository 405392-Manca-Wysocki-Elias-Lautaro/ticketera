package com.auth.app.domain.listeners;

import com.auth.app.domain.events.UserVerifiedEvent;
import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;
import com.auth.app.notification.entity.NotificationChannel;
import com.auth.app.notification.entity.NotificationType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
@Async
public class UserVerifiedListener {

    private final NotificationSender notificationSender;

    @EventListener
    public void handle(UserVerifiedEvent event) {
        var user = event.getUser();

        notificationSender.send(
            NotificationDTO.builder()
                .channel(NotificationChannel.EMAIL.name())
                .type(NotificationType.USER_WELCOME.name())
                .to(user.getEmail())
                .variables(Map.of(
                        "firstName", user.getFirstName(),
                        "link", event.getFrontendUrl() + "/login"
                ))
                .build()
        );

        log.info("[NOTIFICATION] Sent welcome email to verified user {}", user.getEmail());
    }
}
