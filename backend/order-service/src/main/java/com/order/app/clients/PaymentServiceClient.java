package com.order.app.clients;

import com.order.app.pkg.dtos.PaymentRequest;
import com.order.app.pkg.dtos.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.Optional;

@Component
public class PaymentServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceClient.class);
    
    private final RestTemplate restTemplate;
    private final String paymentServiceBaseUrl;
    
    public PaymentServiceClient(RestTemplate restTemplate, 
                               @Value("${services.payment.base-url:http://payment-service:8080}") String paymentServiceBaseUrl) {
        this.restTemplate = restTemplate;
        this.paymentServiceBaseUrl = paymentServiceBaseUrl;
    }
    
    /**
     * Crea una intención de pago en el payment-service
     */
    public Optional<PaymentResponse> createPaymentIntent(PaymentRequest paymentRequest) {
        try {
            logger.info("Creating payment intent for order: {}", paymentRequest.getOrderId());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<PaymentRequest> entity = new HttpEntity<>(paymentRequest, headers);
            
            String url = paymentServiceBaseUrl + "/api/payments/intents";
            
            ResponseEntity<PaymentResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                PaymentResponse.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                logger.info("Payment intent created successfully: {}", response.getBody().getId());
                return Optional.of(response.getBody());
            } else {
                logger.warn("Payment service returned non-success status: {}", response.getStatusCode());
                return Optional.empty();
            }
            
        } catch (HttpClientErrorException e) {
            logger.error("Client error creating payment intent for order {}: {} - {}", 
                paymentRequest.getOrderId(), e.getStatusCode(), e.getResponseBodyAsString());
            return Optional.empty();
            
        } catch (HttpServerErrorException e) {
            logger.error("Server error creating payment intent for order {}: {} - {}", 
                paymentRequest.getOrderId(), e.getStatusCode(), e.getResponseBodyAsString());
            return Optional.empty();
            
        } catch (ResourceAccessException e) {
            logger.error("Connection error to payment service for order {}: {}", 
                paymentRequest.getOrderId(), e.getMessage());
            return Optional.empty();
            
        } catch (Exception e) {
            logger.error("Unexpected error creating payment intent for order {}: {}", 
                paymentRequest.getOrderId(), e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Obtiene el estado de un payment intent
     */
    public Optional<PaymentResponse> getPaymentIntent(Long paymentIntentId) {
        try {
            logger.debug("Getting payment intent: {}", paymentIntentId);
            
            String url = paymentServiceBaseUrl + "/api/payments/intents/" + paymentIntentId;
            
            ResponseEntity<PaymentResponse> response = restTemplate.getForEntity(url, PaymentResponse.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return Optional.of(response.getBody());
            } else {
                logger.warn("Payment service returned non-success status for intent {}: {}", 
                    paymentIntentId, response.getStatusCode());
                return Optional.empty();
            }
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Payment intent not found: {}", paymentIntentId);
            } else {
                logger.error("Client error getting payment intent {}: {} - {}", 
                    paymentIntentId, e.getStatusCode(), e.getResponseBodyAsString());
            }
            return Optional.empty();
            
        } catch (Exception e) {
            logger.error("Error getting payment intent {}: {}", paymentIntentId, e.getMessage(), e);
            return Optional.empty();
        }
    }
    
    /**
     * Cancela un payment intent
     */
    public boolean cancelPaymentIntent(Long paymentIntentId, String reason) {
        try {
            logger.info("Cancelling payment intent: {} with reason: {}", paymentIntentId, reason);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            CancelPaymentRequest cancelRequest = new CancelPaymentRequest(reason);
            HttpEntity<CancelPaymentRequest> entity = new HttpEntity<>(cancelRequest, headers);
            
            String url = paymentServiceBaseUrl + "/api/payments/intents/" + paymentIntentId + "/cancel";
            
            ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                Void.class
            );
            
            boolean success = response.getStatusCode().is2xxSuccessful();
            if (success) {
                logger.info("Payment intent cancelled successfully: {}", paymentIntentId);
            } else {
                logger.warn("Failed to cancel payment intent {}: {}", paymentIntentId, response.getStatusCode());
            }
            
            return success;
            
        } catch (Exception e) {
            logger.error("Error cancelling payment intent {}: {}", paymentIntentId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Verifica si el payment service está disponible
     */
    public boolean isPaymentServiceAvailable() {
        try {
            String url = paymentServiceBaseUrl + "/actuator/health";
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            logger.warn("Payment service is not available: {}", e.getMessage());
            return false;
        }
    }
    
    // DTO interno para cancelación
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class CancelPaymentRequest {
        private String reason;
    }
}
