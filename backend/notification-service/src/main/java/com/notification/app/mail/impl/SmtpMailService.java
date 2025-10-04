package com.notification.app.mail.impl;

import java.util.Map;

import com.notification.app.dto.GenericNotificationDTO;
import com.notification.app.mail.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class SmtpMailService implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String bodyHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(bodyHtml, true);
            helper.setFrom("no-reply@ticketly.com");

            mailSender.send(message);
            log.info("✅ Email sent to {}", to);
        } catch (MessagingException e) {
            log.error("Error sending email: {}", e.getMessage());
        }
    }
}
