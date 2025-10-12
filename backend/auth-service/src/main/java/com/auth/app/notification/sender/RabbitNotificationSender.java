package com.auth.app.notification.sender;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitNotificationSender implements NotificationSender {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.notifications.exchange}")
    private String exchange;

    @Value("${rabbitmq.notifications.key}")
    private String routingKey;

    @Override
    public void send(NotificationDTO notification) {
        log.info("📤 Publicando notificación en RabbitMQ: {}", notification);
        rabbitTemplate.convertAndSend(exchange, routingKey, notification);
    }
}
