package com.payment.app.pkg.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private String id;
    private CustomerResponse customer;
    private String organizerId;
    private String status;  // Usando String en lugar de OrderStatus enum para evitar dependencias
    private Long totalCents;
    private String currency;
    private LocalDateTime expiresAt;
    private String paymentMethod;
    private String notes;
    private String externalReference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    private List<OrderItemResponse> items;
    
    // URL de pago de Mercado Pago (solo presente cuando la orden requiere pago)
    private String paymentUrl;
    
    // Helper methods
    public boolean isPaid() {
        return "PAID".equals(status) && paidAt != null;
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    // Nested classes
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerResponse {
        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private String userId; // UUID string from auth-service
        
        public String getFullName() {
            if (firstName == null && lastName == null) {
                return email;
            }
            return String.format("%s %s", 
                firstName != null ? firstName : "", 
                lastName != null ? lastName : "").trim();
        }
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private String id;
        private String eventId;
        private String venueAreaId;
        private String venueSeatId;
        private String ticketTypeId;
        private Long unitPriceCents;
        private Integer quantity;
        private Long totalPriceCents;
        
        public boolean isForSpecificSeat() {
            return venueSeatId != null;
        }
    }
}
