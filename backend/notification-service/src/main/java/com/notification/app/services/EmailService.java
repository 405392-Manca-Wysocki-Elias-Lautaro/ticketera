package com.notification.app.services;

import com.notification.app.dto.EmailRequest;
import com.notification.app.dto.GenericNotificationDTO;

public interface EmailService {
    public void send(GenericNotificationDTO dto);
    public void sendVerificationEmail(EmailRequest req);
    public void sendUserWelcomeEmail(EmailRequest req);    
}
