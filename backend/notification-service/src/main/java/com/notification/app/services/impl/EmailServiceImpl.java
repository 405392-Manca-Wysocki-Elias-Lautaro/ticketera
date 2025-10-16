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

    /**
     * Envía el correo de verificación de cuenta al usuario.
     * Template: email-verification.html
     * Variables:
     *  - name: nombre del usuario
     *  - link: URL para verificar la cuenta
     */
    @Override
    public void sendVerificationEmail(EmailRequest req) {
        try {
            String html = templateService.render("email-verification", Map.of(
                    "name", req.getFirstName(),
                    "link", req.getLink()
            ));

            mailService.send(req.getTo(), "✨ ¡Confirmá tu cuenta y empezá a disfrutar Ticketly!", html);
        } catch (Exception e) {
            throw new TemplateRenderException(e);
        }
    }

    /**
     * Envía el correo de bienvenida al usuario luego del registro.
     * Template: user-welcome.html
     * Variables:
     *  - name: nombre del usuario
     *  - link: enlace al sitio o panel principal
     */
    @Override
    public void sendUserWelcomeEmail(EmailRequest req) {
        try {
            String html = templateService.render("user-welcome", Map.of(
                    "name", req.getFirstName(),
                    "link", req.getLink()
            ));

            mailService.send(req.getTo(), "🎟️ ¡Bienvenido a Ticketly! Estamos felices de tenerte 🎉", html);
        } catch (Exception e) {
            throw new TemplateRenderException(e);
        }
    }

    /**
     * Notifica al usuario sobre un nuevo inicio de sesión en su cuenta.
     * Template: login-alert.html
     * Variables:
     *  - name: nombre del usuario
     *  - timestamp: fecha y hora del acceso
     *  - ipAddress: IP detectada
     *  - userAgent: información del dispositivo/navegador
     *  - link: enlace para cambiar la contraseña en caso de sospecha
     */
    @Override
    public void sendLoginAlertEmail(EmailRequest req) {
        try {
            String html = templateService.render("login-alert", Map.of(
                    "name", req.getFirstName(),
                    "ipAddress", req.getIpAddress(),
                    "userAgent", req.getUserAgent(),
                    "timestamp", req.getTimestamp(),
                    "link", req.getLink()
            ));

            mailService.send(req.getTo(), "👀 Detectamos un nuevo inicio de sesión en tu cuenta", html);
        } catch (Exception e) {
            throw new TemplateRenderException(e);
        }
    }

    /**
     * Envía el correo con el enlace para restablecer la contraseña.
     * Template: password-reset-request.html
     * Variables:
     *  - name: nombre del usuario
     *  - link: URL para restablecer la contraseña
     *  - ipAddress: IP desde donde se hizo la solicitud
     *  - userAgent: navegador o dispositivo detectado
     *  - timestamp: fecha y hora de la solicitud
     *  - expirationMinutes: tiempo de expiración del enlace
     */
    @Override
    public void sendPasswordResetRequestEmail(EmailRequest req) {
        try {
            String html = templateService.render("password-reset-request", Map.of(
                    "name", req.getFirstName(),
                    "link", req.getLink(),
                    "ipAddress", req.getIpAddress(),
                    "userAgent", req.getUserAgent(),
                    "timestamp", req.getTimestamp(),
                    "expirationMinutes", req.getExpirationMinutes()
            ));

            mailService.send(req.getTo(), "🔒 ¿Olvidaste tu contraseña? Restablecela en Ticketly", html);
        } catch (Exception e) {
            throw new TemplateRenderException(e);
        }
    }

    /**
     * Confirma al usuario que su contraseña fue actualizada exitosamente.
     * Template: password-reset-success.html
     * Variables:
     *  - name: nombre del usuario
     *  - link: enlace para iniciar sesión
     *  - supportEmail: enlace al centro de soporte
     *  - ipAddress: IP del dispositivo
     *  - userAgent: información del navegador o dispositivo
     *  - timestamp: fecha y hora del cambio
     */
    @Override
    public void sendPasswordResetSuccessEmail(EmailRequest req) {
        try {
            String html = templateService.render("password-reset-success", Map.of(
                    "name", req.getFirstName(),
                    "link", req.getLink(),
                    "supportEmail", req.getSupportEmail(),
                    "ipAddress", req.getIpAddress(),
                    "userAgent", req.getUserAgent(),
                    "timestamp", req.getTimestamp()
            ));

            mailService.send(req.getTo(), "✅ Tu contraseña fue actualizada con éxito", html);
        } catch (Exception e) {
            throw new TemplateRenderException(e);
        }
    }
}
