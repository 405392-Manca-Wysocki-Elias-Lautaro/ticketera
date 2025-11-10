package com.payment.app.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_intents", schema = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id", nullable = false)
    private Long orderId;
    
    @Column(name = "provider_id", nullable = false)
    private Long providerId;
    
    @Column(name = "provider_ref", length = 255)
    private String providerRef;
    
    @Column(name = "preference_id", length = 255)
    private String preferenceId;
    
    @Convert(converter = PaymentStatusConverter.class)
    @Column(name = "status", nullable = false, length = 50)
    private PaymentStatus status;
    
    @Column(name = "amount_cents", nullable = false)
    private Long amountCents;
    
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;
    
    @Column(name = "payment_url", length = 500)
    private String paymentUrl;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "customer_email", length = 255)
    private String customerEmail;
    
    @Column(name = "customer_name", length = 255)
    private String customerName;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
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
    }
}
