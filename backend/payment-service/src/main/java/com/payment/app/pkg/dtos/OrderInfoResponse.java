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
    
    private Long id;
    private CustomerInfo customer;
    private Long organizerId;
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
        private Long id;
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
        private Long id;
        private Long eventId;
        private Long venueAreaId;
        private Long venueSeatId;
        private Long ticketTypeId;
        private Long unitPriceCents;
        private Integer quantity;
        private Long totalPriceCents;
    }
}

