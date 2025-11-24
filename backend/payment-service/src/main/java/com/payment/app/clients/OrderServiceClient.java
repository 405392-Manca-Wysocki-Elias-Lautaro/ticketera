package com.payment.app.clients;

import com.payment.app.pkg.dtos.OrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * Cliente para comunicarse con el Order Service
 */
@Component
public class OrderServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final String orderServiceBaseUrl;
    
    public OrderServiceClient(
            RestTemplate restTemplate,
            @Value("${services.order.base-url:http://order-service:8080}") String orderServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.orderServiceBaseUrl = orderServiceBaseUrl;
    }
    
    /**
     * Obtiene la información de una orden por su ID
     */
    public Optional<OrderResponse> getOrderById(String orderId) {
        String url = orderServiceBaseUrl + "/" + orderId;
        
        logger.info("Fetching order from: {}", url);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<ApiResponseWrapper<OrderResponse>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<ApiResponseWrapper<OrderResponse>>() {}
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ApiResponseWrapper<OrderResponse> wrapper = response.getBody();
                // El order-service devuelve success: true/false
                if (wrapper.getSuccess() != null && wrapper.getSuccess() && wrapper.getData() != null) {
                    logger.info("Order {} retrieved successfully", orderId);
                    return Optional.of(wrapper.getData());
                }
            }
            
            logger.warn("Order {} not found or invalid response", orderId);
            return Optional.empty();
            
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Order {} not found", orderId);
            return Optional.empty();
            
        } catch (Exception e) {
            logger.error("Error fetching order {}: {}", orderId, e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * DTO para unwrap la respuesta del API que viene envuelta en un objeto genérico
     * Compatible con ApiResponse del order-service que tiene: success, message, data, timestamp
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class ApiResponseWrapper<T> {
        private Boolean success;  // boolean del order-service
        private String message;
        private T data;
        private java.time.OffsetDateTime timestamp;
        // Campo legacy para compatibilidad
        private Integer status;
    }
}

