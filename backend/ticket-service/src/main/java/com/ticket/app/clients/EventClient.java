package com.ticket.app.clients;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.ticket.app.utils.JwtUtils;

@Component
public class EventClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final JwtUtils jwtUtils;

    public EventClient(
            RestTemplate restTemplate,
            JwtUtils jwtUtils,
            @Value("${external.event.url}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.jwtUtils = jwtUtils;
        this.baseUrl = baseUrl;
    }

    public JsonNode getEventById(UUID eventId) {

        String token = jwtUtils.getCurrentJwt().getTokenValue();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                baseUrl + "/" + eventId,
                HttpMethod.GET,
                entity,
                JsonNode.class
        );

        return response.getBody();
    }
}
