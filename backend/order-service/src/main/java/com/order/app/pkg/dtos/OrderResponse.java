package com.order.app.pkg.dtos;

import com.order.app.models.Order;
import com.order.app.models.OrderItem;
import com.order.app.models.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private Long id;
    private CustomerResponse customer;
    private Long organizerId;
    private OrderStatus status;
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
    
    // Factory method to create from entity
    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
            .id(order.getId())
            .customer(CustomerResponse.fromEntity(order.getCustomer()))
            .organizerId(order.getOrganizerId())
            .status(order.getStatus())
            .totalCents(order.getTotalCents())
            .currency(order.getCurrency())
            .expiresAt(order.getExpiresAt())
            .paymentMethod(order.getPaymentMethod())
            .notes(order.getNotes())
            .externalReference(order.getExternalReference())
            .createdAt(order.getCreatedAt())
            .updatedAt(order.getUpdatedAt())
            .paidAt(order.getPaidAt())
            .items(order.getItems() != null ? 
                order.getItems().stream()
                    .map(OrderItemResponse::fromEntity)
                    .collect(Collectors.toList()) : null)
            .build();
    }
    
    // Helper methods (mantener la lógica de negocio)
    public boolean isPaid() {
        return status == OrderStatus.PAID && paidAt != null;
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
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private Long userId;
        
        public static CustomerResponse fromEntity(com.order.app.models.Customer customer) {
            return CustomerResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phone(customer.getPhone())
                .userId(customer.getUserId())
                .build();
        }
        
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
        private Long id;
        private Long occurrenceId;
        private Long eventVenueAreaId;
        private Long eventVenueSeatId;
        private Long ticketTypeId;
        private Long unitPriceCents;
        private Integer quantity;
        private Long totalPriceCents;
        
        public static OrderItemResponse fromEntity(OrderItem item) {
            return OrderItemResponse.builder()
                .id(item.getId())
                .occurrenceId(item.getOccurrenceId())
                .eventVenueAreaId(item.getEventVenueAreaId())
                .eventVenueSeatId(item.getEventVenueSeatId())
                .ticketTypeId(item.getTicketTypeId())
                .unitPriceCents(item.getUnitPriceCents())
                .quantity(item.getQuantity())
                .totalPriceCents(item.getTotalPriceCents())
                .build();
        }
        
        public boolean isForSpecificSeat() {
            return eventVenueSeatId != null;
        }
    }
}
