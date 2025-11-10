package com.notification.app.strategies.email;

import org.springframework.stereotype.Service;

import com.notification.app.dto.EmailRequest;
import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.entity.NotificationType;
import com.notification.app.exceptions.custom.InvalidTemplateVariablesException;
import com.notification.app.services.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetRequestStrategy implements EmailNotificationStrategy {

    private final EmailService emailService;

    @Override
    public NotificationType getType() {
        return NotificationType.PASSWORD_RESET_REQUEST;
    }

    @Override
    public void send(GenericNotificationDTO dto) {
        try {
            EmailRequest request = EmailRequest.builder()
                    .to(dto.getTo())
                    .firstName((String) dto.getVariables().get("firstName"))
                    .link((String) dto.getVariables().get("link"))
                    .ipAddress((String) dto.getVariables().get("ipAddress"))
                    .userAgent((String) dto.getVariables().get("userAgent"))
                    .timestamp((String) dto.getVariables().get("timestamp"))
                    .expirationMinutes((Integer) dto.getVariables().get("expirationMinutes"))
                    .build();

            emailService.sendPasswordResetRequestEmail(request);
        } catch (Exception e) {
            log.error("❌ Invalid variables for password reset request email: {}", e.getMessage(), e);
            throw new InvalidTemplateVariablesException(e);
        }
    }
}
