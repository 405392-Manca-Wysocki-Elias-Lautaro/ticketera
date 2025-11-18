package com.order.app.pkg.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {
    
    @NotNull(message = "Order ID is required")
    private String orderId;
    
    @NotNull(message = "Provider ID is required")
    @Positive(message = "Provider ID must be positive")
    private Long providerId;
    
    @NotNull(message = "Amount is required")
    @PositiveOrZero(message = "Amount must be positive or zero")
    private Long amountCents;
    
    @NotNull(message = "Currency is required")
    private String currency;
    
    private String providerRef;
    private PaymentMetadata metadata;
    
    // Constructor personalizado para casos comunes
    public PaymentRequest(UUID orderId, Long providerId, Long amountCents, String currency) {
        this.orderId = orderId != null ? orderId.toString() : null;
        this.providerId = providerId;
        this.amountCents = amountCents;
        this.currency = currency;
    }
    
    // Nested class for metadata
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentMetadata {
        private String customerEmail;
        private String customerName;
        private String description;
        private String returnUrl;
        private String cancelUrl;
    }
}
