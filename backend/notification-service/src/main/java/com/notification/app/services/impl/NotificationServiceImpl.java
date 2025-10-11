package com.notification.app.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.exceptions.custom.NotificationProcessingException;
import com.notification.app.exceptions.custom.UnsupportedChannelException;
import com.notification.app.services.NotificationService;
import com.notification.app.strategies.NotificationChannelStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final List<NotificationChannelStrategy> strategies;

    @Override
    public void send(GenericNotificationDTO dto) {
        log.info("📢 Sending notification via {} to {} link {}", dto.getChannel(), dto.getTo(), dto.getVariables());

        try {
            strategies.stream()
                    .filter(s -> s.getChannelType() == dto.getChannel())
                    .findFirst()
                    .orElseThrow(UnsupportedChannelException::new)
                    .send(dto);
        } catch (Exception e) {
            log.error("Error processing notification via {}: {}", dto.getChannel(), e.getMessage(), e);
            throw new NotificationProcessingException(e);
        }
    }
}
