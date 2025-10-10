package com.notification.app.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.notification.app.dto.GenericNotificationDTO;
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

        strategies.stream()
            .filter(s -> s.getChannelType() == dto.getChannel())
            .findFirst()
            .orElseThrow(() ->
                new UnsupportedOperationException("Unsupported channel: " + dto.getChannel()))
            .send(dto);
    }
}
