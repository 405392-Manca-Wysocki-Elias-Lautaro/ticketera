package com.notification.app.services;

import com.notification.app.dto.EmailRequest;
import com.notification.app.dto.GenericNotificationDTO;

public interface EmailService {
    void send(GenericNotificationDTO dto);
    void sendVerificationEmail(EmailRequest req);
    void sendUserWelcomeEmail(EmailRequest req);  
    void sendLoginAlertEmail(EmailRequest req);  
}
