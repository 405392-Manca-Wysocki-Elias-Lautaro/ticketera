package com.notification.app.mail;

public interface MailService {
    void send(String to, String subject, String bodyHtml);
}

