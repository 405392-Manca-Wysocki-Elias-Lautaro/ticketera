package com.notification.app.strategies.email;

import org.springframework.stereotype.Service;

import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.entity.NotificationType;
import com.notification.app.services.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventReminderStrategy implements EmailNotificationStrategy {

    private final EmailService emailService;

    @Override
    public NotificationType getType() {
        return NotificationType.EVENT_REMINDER;
    }

    @Override
    public void send(GenericNotificationDTO dto) {
        log.info("Sending event reminder email to {}", dto.getTo());
        emailService.send(dto);
    }
}
