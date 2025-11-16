package com.payment.app.clients;

import com.payment.app.pkg.dtos.GenerateTicketRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente para comunicarse con el Ticket Service
 */
@Component
public class TicketServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final String ticketServiceBaseUrl;
    
    public TicketServiceClient(
            RestTemplate restTemplate,
            @Value("${services.ticket.base-url:http://ticket-service:8080}") String ticketServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.ticketServiceBaseUrl = ticketServiceBaseUrl;
    }
    
    /**
     * Genera un ticket
     */
    public boolean generateTicket(GenerateTicketRequest request) {
        String url = ticketServiceBaseUrl + "/generate";
        
        logger.info("Generating ticket at: {} for orderItem: {}", url, request.getOrderItemId());
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GenerateTicketRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Ticket generated successfully for orderItem: {}", request.getOrderItemId());
                return true;
            }
            
            logger.warn("Failed to generate ticket for orderItem: {}. Status: {}", 
                    request.getOrderItemId(), response.getStatusCode());
            return false;
            
        } catch (Exception e) {
            logger.error("Error generating ticket for orderItem {}: {}", 
                    request.getOrderItemId(), e.getMessage(), e);
            return false;
        }
    }
}

