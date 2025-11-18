package com.payment.app.pkg.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para recibir información de una orden desde el Order Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoResponse {
    
    private String id;
    private CustomerInfo customer;
    private String organizerId;
    private String status;
    private Long totalCents;
    private String currency;
    private LocalDateTime expiresAt;
    private String paymentMethod;
    private String notes;
    private String externalReference;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;
    private List<OrderItemInfo> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private String userId; // UUID String
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemInfo {
        private String id;  // UUID como String
        private String eventId;  // UUID como String
        private String venueAreaId;  // UUID como String
        private String venueSeatId;  // UUID como String (puede ser null)
        private String ticketTypeId;  // UUID como String
        private Long unitPriceCents;
        private Integer quantity;
        private Long totalPriceCents;
    }
}

