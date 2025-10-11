package com.notification.app.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;


@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "notifications.exchange";
    public static final String QUEUE = "notifications.queue";
    public static final String ROUTING_KEY = "notifications.key";

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue)
                .to(notificationExchange)
                .with(ROUTING_KEY);
    }

}