package com.notification.app.controllers;

import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.services.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Recibe una notificación genérica y la despacha al canal correspondiente.
     * Ejemplo de body:
     * {
     *   "channel": "EMAIL",
     *   "type": "EMAIL_VERIFICATION",
     *   "to": "juan@ticketera.com",
     *   "subject": "Verificá tu cuenta",
     *   "template": "email-verification",
     *   "variables": { "firstName": "Juan", "token": "abc123" }
     * }
     */
    @PostMapping
    public ResponseEntity<String> sendNotification(@Validated @RequestBody GenericNotificationDTO dto) {
        log.info("📨 New notification request received: type={}, channel={}, to={}",
                dto.getType(), dto.getChannel(), dto.getTo());

        try {
            notificationService.send(dto);
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body("Notification accepted for processing");
        } catch (UnsupportedOperationException ex) {
            log.error("❌ Unsupported channel: {}", dto.getChannel());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Unsupported channel: " + dto.getChannel());
        } catch (Exception ex) {
            log.error("❌ Failed to send notification: {}", ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process notification");
        }
    }
}
