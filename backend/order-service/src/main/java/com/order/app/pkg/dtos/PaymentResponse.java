package com.order.app.pkg.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    
    private Long id;
    private Long orderId;
    private Long providerId;
    private String providerRef;
    private PaymentStatus status;
    private Long amountCents;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String paymentUrl; // URL for redirect-based payments
    private String errorMessage;
    
    // Helper methods (mantener la lógica de negocio)
    public boolean isSuccessful() {
        return status == PaymentStatus.CAPTURED || status == PaymentStatus.AUTHORIZED;
    }
    
    public boolean isFailed() {
        return status == PaymentStatus.FAILED || status == PaymentStatus.CANCELED;
    }
    
    public boolean requiresRedirect() {
        return paymentUrl != null && !paymentUrl.isEmpty();
    }
    
    // Enum for payment status
    public enum PaymentStatus {
        PENDING("pending"),
        AUTHORIZED("authorized"),
        CAPTURED("captured"),
        FAILED("failed"),
        CANCELED("canceled");
        
        private final String value;
        
        PaymentStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static PaymentStatus fromValue(String value) {
            for (PaymentStatus status : PaymentStatus.values()) {
                if (status.value.equals(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid payment status: " + value);
        }
    }
}
