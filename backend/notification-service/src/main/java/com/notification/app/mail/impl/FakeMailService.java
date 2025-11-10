package com.notification.app.mail.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.notification.app.exceptions.custom.EmailSendException;
import com.notification.app.mail.MailService;

import lombok.extern.slf4j.Slf4j;

@Service
@Profile("test")
@Slf4j
public class FakeMailService implements MailService {

    @Override
    public void send(String to, String subject, String bodyHtml) {
        try {
            log.info("📨 [FAKE MAIL] To: {}, Subject: {}", to, subject);
            log.debug("Body:\n{}", bodyHtml);
        } catch (Exception e) {
            log.error("Error emulating email send: {}", e.getMessage(), e);
            throw new EmailSendException(e);
        }
    }
}
