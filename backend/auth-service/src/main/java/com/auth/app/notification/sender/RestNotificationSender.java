package com.auth.app.notification.sender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.auth.app.notification.NotificationSender;
import com.auth.app.notification.dto.NotificationDTO;

@Component
@Profile("dev")
public class RestNotificationSender implements NotificationSender {

    private final RestTemplate restTemplate;
    private final String notificationUrl;

    public RestNotificationSender(@Value("${notification.url}") String notificationUrl) {
        this.notificationUrl = notificationUrl;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void send(NotificationDTO notification) {
        String endpoint = notificationUrl + "/api/notifications";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<NotificationDTO> request = new HttpEntity<>(notification, headers);

        restTemplate.postForEntity(endpoint, request, Void.class);
    }
}
