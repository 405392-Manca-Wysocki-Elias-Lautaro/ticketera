package com.notification.app.strategies.email;

import java.util.List;
import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.entity.NotificationChannel;
import com.notification.app.entity.NotificationType;
import com.notification.app.exceptions.custom.InvalidNotificationChannelException;
import com.notification.app.exceptions.custom.UnsupportedNotificationTypeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import com.notification.app.strategies.NotificationChannelStrategy;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailChannelStrategy implements NotificationChannelStrategy {

    private final List<EmailNotificationStrategy> emailStrategies;

    @Override
    public NotificationChannel getChannelType() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(GenericNotificationDTO dto) {
        NotificationType type = dto.getType();
        NotificationChannel channel = dto.getChannel();

        if (!type.supports(channel)) {
            throw new InvalidNotificationChannelException();
        }

        emailStrategies.stream()
                .filter(s -> s.getType().equals(dto.getType()))
                .findFirst()
                .orElseThrow(UnsupportedNotificationTypeException::new)
                .send(dto);
    }
}
