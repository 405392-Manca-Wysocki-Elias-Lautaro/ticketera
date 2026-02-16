package com.ticket.app.clients;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SystemEventClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    // TODO: Store in env/config server
    private static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";
    private static final String INTERNAL_SECRET_VALUE = "TICKETERA_INTERNAL_SECRET_2024";

    public SystemEventClient(
            RestTemplate restTemplate,
            @Value("${external.event.url}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<EventSummaryDTO> getUpcomingEvents(LocalDateTime start, LocalDateTime end) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(INTERNAL_SECRET_HEADER, INTERNAL_SECRET_VALUE);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String url = String.format("%s/internal/events/upcoming?start=%s&end=%s", 
                    baseUrl, start.toString(), end.toString());

            ResponseEntity<ApiResponse<List<EventSummaryDTO>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<ApiResponse<List<EventSummaryDTO>>>() {}
            );

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }
        } catch (Exception e) {
            log.error("Failed to fetch upcoming events", e);
        }
        return Collections.emptyList();
    }

    @Data
    @NoArgsConstructor
    public static class EventSummaryDTO {
        private java.util.UUID id;
        private String title;
        private LocalDateTime startsAt;
        private String venueName;
    }
    
    // Helper wrapper to matching ApiResponse structure
    @Data
    @NoArgsConstructor
    private static class ApiResponse<T> {
        private String message;
        private T data;
    }
}
