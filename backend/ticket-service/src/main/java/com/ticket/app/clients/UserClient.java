package com.ticket.app.clients;

import java.util.UUID;

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
public class UserClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";
    private static final String INTERNAL_SECRET_VALUE = "TICKETERA_INTERNAL_SECRET_2024";

    public UserClient(
            RestTemplate restTemplate,
            @Value("${external.auth.url}") String baseUrl
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public UserResponse getUserById(UUID userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(INTERNAL_SECRET_HEADER, INTERNAL_SECRET_VALUE);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String url = String.format("%s/internal/users/%s", baseUrl, userId.toString());

            ResponseEntity<ApiResponse<UserResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<ApiResponse<UserResponse>>() {}
            );

            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }
        } catch (Exception e) {
            log.error("Failed to fetch user {}", userId, e);
        }
        return null;
    }

    @Data
    @NoArgsConstructor
    public static class UserResponse {
        private UUID id;
        private String email;
        private String firstName;
        private String lastName;
    }
    
    @Data
    @NoArgsConstructor
    private static class ApiResponse<T> {
        private String message;
        private T data;
    }
}
