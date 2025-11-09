package com.payment.app.pkg.dtos;

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
    private String status;
    private Long amountCents;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String paymentUrl; // URL for redirect to Mercado Pago Checkout
    private String errorMessage;
}

