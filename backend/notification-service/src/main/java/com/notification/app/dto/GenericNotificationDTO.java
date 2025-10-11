package com.notification.app.dto;

import java.util.Map;

import com.notification.app.entity.NotificationChannel;
import com.notification.app.entity.NotificationType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenericNotificationDTO {

    /** Canal de envío: EMAIL, SMS, PUSH, WHATSAPP, etc. */
    @NotNull(message = "El canal de notificación es obligatorio")
    private NotificationChannel channel;

    /** Tipo de notificación: WELCOME, PASSWORD_RESET, etc. */
    @NotNull(message = "El tipo de notificación es obligatorio")
    private NotificationType type;

    /** Destinatario (correo, número de teléfono o ID de usuario) */
    @NotBlank(message = "El destinatario ('to') es obligatorio")
    private String to;

    /** Asunto del mensaje (solo relevante para EMAIL) */
    @Size(max = 255, message = "El asunto no puede superar los 255 caracteres")
    private String subject;

    /** Nombre del template (por ejemplo: "email-verification") */
    @Size(max = 100, message = "El nombre del template no puede superar los 100 caracteres")
    private String template;

    /** Mensaje plano o HTML (para casos sin template) */
    private String message;

    /** Variables dinámicas para renderizar la plantilla */
    private Map<String, Object> variables;

    /** Información adicional (tracking, auditoría, etc.) */
    private Map<String, Object> metadata;
}
