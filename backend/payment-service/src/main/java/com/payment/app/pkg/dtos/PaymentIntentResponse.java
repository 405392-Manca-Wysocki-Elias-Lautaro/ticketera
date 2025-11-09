package com.payment.app.pkg.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntentResponse {
    
    private Long id;
    private Long orderId;
    private Long providerId;
    private String providerRef;
    
    // Devuelve el status como String en formato compatible con el enum del order-service
    @JsonProperty("status")
    private String status;
    
    private Long amountCents;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String paymentUrl; // URL for redirect to Mercado Pago Checkout
    private String errorMessage;
    
    // Helper methods para compatibilidad con order-service
    public boolean isSuccessful() {
        return "CAPTURED".equals(status) || "AUTHORIZED".equals(status);
    }
    
    public boolean isFailed() {
        return "FAILED".equals(status) || "CANCELED".equals(status);
    }
    
    public boolean requiresRedirect() {
        return paymentUrl != null && !paymentUrl.isEmpty();
    }
}

