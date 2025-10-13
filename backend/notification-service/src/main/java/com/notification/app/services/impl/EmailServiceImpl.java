package com.notification.app.services.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.notification.app.dto.EmailRequest;
import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.exceptions.custom.NotificationProcessingException;
import com.notification.app.exceptions.custom.TemplateRenderException;
import com.notification.app.mail.MailService;
import com.notification.app.services.EmailService;
import com.notification.app.services.TemplateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final MailService mailService;
    private final TemplateService templateService;

    @Override
    public void send(GenericNotificationDTO dto) {
        try {
            String body = templateService.render(dto.getTemplate(), dto.getVariables());
            mailService.send(dto.getTo(), dto.getSubject(), body);
        } catch (Exception e) {
            throw new NotificationProcessingException(e);
        }
    }

    @Override
    public void sendVerificationEmail(EmailRequest req) {
        try {
            String html = templateService.render("email-verification", Map.of(
                    "name", req.getFirstName(),
                    "link", req.getLink()
            ));

            mailService.send(req.getTo(), "Verificá tu cuenta", html);
        } catch (Exception e) {
            throw new TemplateRenderException(e);
        }
    }

    @Override
    public void sendUserWelcomeEmail(EmailRequest req) {
        try {
            String html = templateService.render("user-welcome", Map.of(
                    "name", req.getFirstName(),
                    "link", req.getLink()
            ));

            mailService.send(req.getTo(), "¡Bienvenido a Ticketly!", html);
        } catch (Exception e) {
            throw new TemplateRenderException(e);
        }
    }

    @Override
    public void sendLoginAlertEmail(EmailRequest req) {
        try {
            log.info("request: {}", req);
            String html = templateService.render("login-alert", Map.of(
                    "name", req.getFirstName(),
                    "ipAddress", req.getIpAddress(),
                    "userAgent", req.getUserAgent(),
                    "timestamp", req.getTimestamp(),
                    "link", req.getLink()
            ));

            mailService.send(req.getTo(), "Nuevo inicio de sesión detectado en tu cuenta Ticketly", html);
        } catch (Exception e) {
            throw new TemplateRenderException(e);
        }
    }
}
