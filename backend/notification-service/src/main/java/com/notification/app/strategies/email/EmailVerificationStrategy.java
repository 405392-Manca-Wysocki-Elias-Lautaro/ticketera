package com.notification.app.strategies.email;

import org.springframework.stereotype.Service;

import com.notification.app.dto.EmailRequest;
import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.services.EmailService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailVerificationStrategy implements EmailNotificationStrategy {

    private final EmailService emailService;

    public EmailVerificationStrategy(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public String getType() {
        return "EMAIL_VERIFICATION";
    }

    @Override
    public void send(GenericNotificationDTO dto) {
        emailService.sendVerificationEmail(
            EmailRequest.builder()
                .to(dto.getTo())
                .firstName((String) dto.getVariables().get("firstName"))
                .firstName((String) dto.getVariables().get("lastName"))
                .link((String) dto.getVariables().get("link"))
                .build()
        );
    }
}
