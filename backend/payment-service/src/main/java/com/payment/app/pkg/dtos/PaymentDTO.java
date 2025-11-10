package com.payment.app.pkg.dtos;

import com.payment.app.models.Payment;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    
    private Long id;
    private Long orderId;
    private Long providerId;
    private String providerRef;
    private String preferenceId;
    private String status;
    private Long amountCents;
    private String currency;
    private String paymentUrl;
    private String errorMessage;
    private String customerEmail;
    private String customerName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Método para convertir desde la entidad
    public static PaymentDTO fromEntity(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .providerId(payment.getProviderId())
                .providerRef(payment.getProviderRef())
                .preferenceId(payment.getPreferenceId())
                .status(payment.getStatus().name())
                .amountCents(payment.getAmountCents())
                .currency(payment.getCurrency())
                .paymentUrl(payment.getPaymentUrl())
                .errorMessage(payment.getErrorMessage())
                .customerEmail(payment.getCustomerEmail())
                .customerName(payment.getCustomerName())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
