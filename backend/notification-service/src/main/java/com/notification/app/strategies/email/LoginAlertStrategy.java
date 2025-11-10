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
public class LoginAlertStrategy implements EmailNotificationStrategy {

    private final EmailService emailService;

    @Override
    public NotificationType getType() {
        return NotificationType.LOGIN_ALERT;
    }

    @Override
    public void send(GenericNotificationDTO dto) {
        try {
            EmailRequest request = EmailRequest.builder()
                    .to(dto.getTo())
                    .firstName((String) dto.getVariables().get("firstName"))
                    .timestamp((String) dto.getVariables().get("timestamp"))
                    .ipAddress((String) dto.getVariables().get("ipAddress"))
                    .userAgent((String) dto.getVariables().get("userAgent"))
                    .timestamp((String) dto.getVariables().get("timestamp"))
                    .link((String) dto.getVariables().get("link"))
                    .build();

            emailService.sendLoginAlertEmail(request);
        } catch (Exception e) {
            log.error("❌ Invalid variables for login alert email: {}", e.getMessage(), e);
            throw new InvalidTemplateVariablesException(e);
        }
    }

}
