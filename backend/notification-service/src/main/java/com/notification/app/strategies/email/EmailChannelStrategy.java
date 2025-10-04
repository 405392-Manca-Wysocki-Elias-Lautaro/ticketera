package com.notification.app.strategies.email;

import java.util.List;
import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.entity.NotificationChannel;
import com.notification.app.entity.NotificationType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.notification.app.strategies.NotificationChannelStrategy;

@Component
@RequiredArgsConstructor
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
            throw new IllegalArgumentException(
                    "❌ Invalid channel " + channel + " for notification type " + type
            );
        }

        emailStrategies.stream()
                .filter(s -> s.getType().equalsIgnoreCase(dto.getType().name()))
                .findFirst()
                .orElseThrow(()
                        -> new UnsupportedOperationException("Unsupported email type: " + dto.getType()))
                .send(dto);
    }
}
