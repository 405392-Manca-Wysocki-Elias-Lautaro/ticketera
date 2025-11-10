package com.notification.app.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.MessageConverter;


@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "notifications.exchange";
    public static final String QUEUE = "notifications.queue";
    public static final String DLQ = "notifications.queue.dlq";
    public static final String ROUTING_KEY = "notifications.key";

    /**
     * Exchange principal (Direct o Topic según tu uso actual)
     */
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    /**
     * Cola principal con referencia a la DLQ
     */
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", "") // exchange vacío = default exchange
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    /**
     * Dead Letter Queue (cola de mensajes fallidos)
     */
    @Bean
    public Queue notificationDeadLetterQueue() {
        return QueueBuilder.durable(DLQ).build();
    }

    /**
     * Binding entre cola principal y exchange
     */
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(notificationExchange())
                .with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setDefaultRequeueRejected(false); // evitar reintentos infinitos
        return factory;
    }

}
