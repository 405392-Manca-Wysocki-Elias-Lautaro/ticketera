package com.order.app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders", schema = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(exclude = {"items", "statusHistory", "customer"}) // Evitar lazy loading issues
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @NotNull
    private Customer customer;
    
    @Column(name = "organizer_id", nullable = false)
    @NotNull
    private Long organizerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "total_cents", nullable = false)
    @PositiveOrZero
    @Builder.Default
    private Long totalCents = 0L;
    
    @Column(nullable = false)
    @Builder.Default
    private String currency = "ARS";
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    private String notes;
    
    @Column(name = "external_reference")
    private String externalReference;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "paid_at")
    private LocalDateTime paidAt;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderStatusHistory> statusHistory;
    
    // Business methods (mantener la lógica de negocio)
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isPaid() {
        return status == OrderStatus.PAID && paidAt != null;
    }
    
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.FAILED;
    }
    
    public boolean canBeRefunded() {
        return status == OrderStatus.PAID;
    }
    
    public void calculateTotal() {
        if (items != null && !items.isEmpty()) {
            this.totalCents = items.stream()
                .mapToLong(item -> item.getUnitPriceCents() * item.getQuantity())
                .sum();
        }
    }
    
    public void markAsPaid(String paymentMethod) {
        this.status = OrderStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.paymentMethod = paymentMethod;
    }
    
    public void markAsFailed() {
        this.status = OrderStatus.FAILED;
    }
    
    public void markAsRefunded() {
        this.status = OrderStatus.REFUNDED;
    }
}
