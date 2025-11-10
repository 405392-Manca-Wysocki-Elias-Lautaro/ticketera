package com.notification.app.listeners;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.exceptions.custom.NotificationProcessingException;
import com.notification.app.services.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    /**
     * Escucha los mensajes que llegan a la cola de notificaciones.
     * Cada mensaje debe ser un JSON que represente un GenericNotificationDTO.
     */
    @RabbitListener(queues = "${rabbitmq.notifications.queue}")
    public void handleNotification(@Payload GenericNotificationDTO dto) {
        try {
            log.info("📩 Mensaje recibido desde RabbitMQ: {}", dto);

            // Procesar la notificación (EMAIL, SMS, etc.)
            notificationService.send(dto);

            log.info("✅ Notificación procesada correctamente para {}", dto.getTo());

        } catch (NotificationProcessingException e) {
            log.error("⚠️ Error procesando notificación: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ Error general en NotificationListener: {}", e.getMessage(), e);
        }
    }
}
