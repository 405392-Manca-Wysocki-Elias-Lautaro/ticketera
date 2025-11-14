package com.order.app.pkg.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public class CreateOrderRequest {
    
    @Valid
    @NotNull(message = "Customer information is required")
    private CustomerInfo customer;
    
    @NotNull(message = "Organizer ID is required")
    @Positive(message = "Organizer ID must be positive")
    private Long organizerId;
    
    @Valid
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequest> items;
    
    @Size(max = 3, message = "Currency code must be 3 characters")
    private String currency = "ARS";
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
    
    @Size(max = 100, message = "External reference cannot exceed 100 characters")
    private String externalReference;
    
    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expiresAt;
    
    @Size(max = 200, message = "Payment description cannot exceed 200 characters")
    private String paymentDescription;
    
    // Constructors
    public CreateOrderRequest() {}
    
    // Getters and Setters
    public CustomerInfo getCustomer() { return customer; }
    public void setCustomer(CustomerInfo customer) { this.customer = customer; }
    
    public Long getOrganizerId() { return organizerId; }
    public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }
    
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getExternalReference() { return externalReference; }
    public void setExternalReference(String externalReference) { this.externalReference = externalReference; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getPaymentDescription() { return paymentDescription; }
    public void setPaymentDescription(String paymentDescription) { this.paymentDescription = paymentDescription; }
    
    // Nested classes
    public static class CustomerInfo {
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        private String email;
        
        @Size(max = 100, message = "First name cannot exceed 100 characters")
        private String firstName;
        
        @Size(max = 100, message = "Last name cannot exceed 100 characters")
        private String lastName;
        
        // Aceptar formato internacional (+549...) o nacional (0351...)
        @Pattern(regexp = "^(\\+?[1-9]\\d{1,14}|0\\d{9,14})$", message = "Invalid phone number format")
        private String phone;
        
        private String userId; // Optional, for registered users (UUID string from auth-service)
        
        // Constructors
        public CustomerInfo() {}
        
        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
    
    public static class OrderItemRequest {
        @NotNull(message = "Event ID is required")
        @Positive(message = "Event ID must be positive")
        private Long eventId;
        
        private Long venueAreaId;
        
        private Long venueSeatId;
        
        @NotNull(message = "Ticket type ID is required")
        @Positive(message = "Ticket type ID must be positive")
        private Long ticketTypeId;
        
        @NotNull(message = "Unit price is required")
        @PositiveOrZero(message = "Unit price must be positive or zero")
        private Long unitPriceCents;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity = 1;
        
        // Constructors
        public OrderItemRequest() {}
        
        // Getters and Setters
        public Long getEventId() { return eventId; }
        public void setEventId(Long eventId) { this.eventId = eventId; }
        
        public Long getVenueAreaId() { return venueAreaId; }
        public void setVenueAreaId(Long venueAreaId) { this.venueAreaId = venueAreaId; }
        
        public Long getVenueSeatId() { return venueSeatId; }
        public void setVenueSeatId(Long venueSeatId) { this.venueSeatId = venueSeatId; }
        
        public Long getTicketTypeId() { return ticketTypeId; }
        public void setTicketTypeId(Long ticketTypeId) { this.ticketTypeId = ticketTypeId; }
        
        public Long getUnitPriceCents() { return unitPriceCents; }
        public void setUnitPriceCents(Long unitPriceCents) { this.unitPriceCents = unitPriceCents; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        // Validation method
        public void validate() {
            if (venueSeatId != null && quantity != 1) {
                throw new IllegalArgumentException("Specific seat items must have quantity = 1");
            }
        }
    }
}
