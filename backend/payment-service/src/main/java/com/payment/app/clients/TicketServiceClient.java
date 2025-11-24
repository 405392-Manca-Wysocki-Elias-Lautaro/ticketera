package com.payment.app.clients;

import com.payment.app.pkg.dtos.GenerateTicketRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
            // Validar campos requeridos antes de procesar
            if (request.getOrderItemId() == null || request.getOrderItemId().trim().isEmpty()) {
                logger.error("OrderItemId is null or empty for ticket generation");
                return false;
            }
            
            if (request.getOccurrenceId() == null || request.getOccurrenceId().trim().isEmpty()) {
                logger.error("OccurrenceId (eventId) is null or empty for orderItem: {}", request.getOrderItemId());
                return false;
            }
            
            if (request.getVenueAreaId() == null || request.getVenueAreaId().trim().isEmpty()) {
                logger.error("VenueAreaId is null or empty for orderItem: {}", request.getOrderItemId());
                return false;
            }
            
            if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
                logger.error("UserId is null or empty for orderItem: {}", request.getOrderItemId());
                return false;
            }
            
            // Mapear GenerateTicketRequest a TicketGenerateRequest (formato esperado por ticket-service)
            Map<String, Object> ticketRequest = new HashMap<>();
            ticketRequest.put("orderItemId", UUID.fromString(request.getOrderItemId()));
            ticketRequest.put("eventId", UUID.fromString(request.getOccurrenceId())); // occurrenceId -> eventId
            ticketRequest.put("eventVenueAreaId", UUID.fromString(request.getVenueAreaId()));
            ticketRequest.put("eventVenueSeatId", null); // Para general admission
            ticketRequest.put("userId", UUID.fromString(request.getUserId()));
            ticketRequest.put("price", request.getPrice());
            ticketRequest.put("currency", request.getCurrency());
            ticketRequest.put("discount", request.getDiscount());
            ticketRequest.put("finalPrice", request.getFinalPrice());
            ticketRequest.put("eventStart", request.getEventStart());
            ticketRequest.put("eventEnd", request.getEventEnd());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(ticketRequest, headers);
            
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

