package com.auth.app.domain.listeners;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.auth.app.domain.events.UserRegisteredEvent;
import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;
import com.auth.app.notification.entity.NotificationChannel;
import com.auth.app.notification.entity.NotificationType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Async
public class UserRegisteredListener {

    private final NotificationSender notificationSender;

    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        notificationSender.send(NotificationDTO.builder()
                .channel(NotificationChannel.EMAIL)
                .type(NotificationType.EMAIL_VERIFICATION)
                .to(event.getUser().getEmail())
                .variables(Map.of(
                        "firstName", event.getUser().getFirstName(),
                        "link", event.getVerificationLink()
                ))
                .build());
    }
}
