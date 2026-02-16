package com.ticket.app.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public NotificationClient(
            RestTemplate restTemplate,
            @Value("${external.notification.url}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public void sendNotification(GenericNotificationDTO notification) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<GenericNotificationDTO> entity = new HttpEntity<>(notification, headers);

            restTemplate.postForEntity(baseUrl, entity, String.class);
            log.info("Notification sent successfully to {}", notification.getTo());
        } catch (Exception e) {
            log.error("Failed to send notification to {}", notification.getTo(), e);
        }
    }

    @Data
    @Builder
    public static class GenericNotificationDTO {
        private String channel; // EMAIL
        private String type;
        private String to;
        private String subject;
        private String template;
        private java.util.Map<String, Object> variables;
    }
}
