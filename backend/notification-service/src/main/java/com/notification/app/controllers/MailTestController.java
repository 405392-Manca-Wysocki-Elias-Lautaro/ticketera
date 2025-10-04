package com.notification.app.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.entity.NotificationChannel;
import com.notification.app.entity.NotificationType;
import com.notification.app.services.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test-mail")
@RequiredArgsConstructor
public class MailTestController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<String> testMail() {

        /**
         * Recibe una notificación genérica y la despacha al canal
         * correspondiente. Ejemplo de body: { "channel": "EMAIL", "type":
         * "EMAIL_VERIFICATION", "to": "juan@ticketera.com", "subject":
         * "Verificá tu cuenta", "template": "email-verification", "variables":
         * { "firstName": "Juan", "token": "abc123" } }
         */
        GenericNotificationDTO email = new GenericNotificationDTO();

        email.setChannel(NotificationChannel.EMAIL);
        email.setType(NotificationType.EMAIL_VERIFICATION);
        email.setTo("test@local.dev");
        email.setSubject("Testing");
        email.setMessage("https://ticketera.dev/verify?token=abc123");
        email.setTemplate("email-verification");
        email.setVariables(Map.of(
            "firstName", "Juan",
            "token", "abc123"
        ));

        notificationService.send(email);
        return ResponseEntity.ok("Mail enviado 🚀. Revisá MailHog.");
    }
}
