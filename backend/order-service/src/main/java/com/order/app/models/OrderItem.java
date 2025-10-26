package com.order.app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_items", schema = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(exclude = "order") // Evitar lazy loading issues
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private Order order;
    
    @Column(name = "occurrence_id", nullable = false)
    @NotNull
    private Long occurrenceId;
    
    @Column(name = "event_venue_area_id")
    private Long eventVenueAreaId;
    
    @Column(name = "event_venue_seat_id")
    private Long eventVenueSeatId;
    
    @Column(name = "ticket_type_id", nullable = false)
    @NotNull
    private Long ticketTypeId;
    
    @Column(name = "unit_price_cents", nullable = false)
    @PositiveOrZero
    private Long unitPriceCents;
    
    @Column(nullable = false)
    @Positive
    @Builder.Default
    private Integer quantity = 1;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    
    // Business methods (mantener la lógica de negocio)
    public Long getTotalPriceCents() {
        return unitPriceCents * quantity;
    }
    
    public boolean isForSpecificSeat() {
        return eventVenueSeatId != null;
    }
    
    public boolean isGeneralAdmission() {
        return eventVenueSeatId == null;
    }
    
    public void validateSeatAndQuantity() {
        if (eventVenueSeatId != null && quantity != 1) {
            throw new IllegalStateException("Specific seat items must have quantity = 1");
        }
        if (eventVenueSeatId == null && quantity <= 0) {
            throw new IllegalStateException("General admission items must have quantity > 0");
        }
    }
}
