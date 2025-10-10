package com.notification.app.services.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.notification.app.dto.EmailRequest;
import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.mail.MailService;
import com.notification.app.services.EmailService;
import com.notification.app.services.TemplateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private final MailService mailService;
    private final TemplateService templateService;

    @Override
    public void send(GenericNotificationDTO dto) {
        String body = templateService.render(dto.getTemplate(), dto.getVariables());
        mailService.send(dto.getTo(), dto.getSubject(), body);
    }

    @Override
    public void sendVerificationEmail(EmailRequest req) {
        try {
            String html = templateService.render("email-verification.html", Map.of(
                    "name", req.getFirstName(),
                    // "lastName", req.getLastName(),
                    "link", req.getLink()
            ));

            mailService.send(req.getTo(), "Verificá tu cuenta", html);
        } catch (Exception e) {

            throw new RuntimeException("Error loading email template", e);
        }
    }
}
