package com.notification.app.mail.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.notification.app.mail.MailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import lombok.extern.slf4j.Slf4j;

@Service
@Profile("prod")
@Slf4j
public class SendGridMailService implements MailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Override
    public void send(String to, String subject, String bodyHtml) {
        Email from = new Email("no-reply@tuapp.com");
        Email recipient = new Email(to);
        Content content = new Content("text/html", bodyHtml);
        Mail mail = new Mail(from, subject, recipient, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info("📧 Mail sent to {} (status {})", to, response.getStatusCode());
        } catch (IOException e) {
            log.error("Error sending mail to {}: {}", to, e.getMessage());
            throw new RuntimeException("Error sending mail", e);
        }
    }
}
