package com.auth.app.notification.sender;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;

@Component
@Profile({"test", "prod"})
public class RabbitNotificationSender implements NotificationSender {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public RabbitNotificationSender(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange}") String exchange,
            @Value("${rabbitmq.routing-key}") String routingKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    @Override
    public void send(NotificationDTO notification) {
        rabbitTemplate.convertAndSend(exchange, routingKey, notification);
    }
}
