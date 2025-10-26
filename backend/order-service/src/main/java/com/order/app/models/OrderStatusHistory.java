package com.order.app.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status_history", schema = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(exclude = "order") // Evitar lazy loading issues
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderStatusHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private Order order;
    
    @Column(name = "from_status")
    private String fromStatus;
    
    @Column(name = "to_status", nullable = false)
    @NotNull
    private String toStatus;
    
    @Column(name = "changed_by")
    private Long changedBy;
    
    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;
    
    private String note;
    
    // Static factory methods
    public static OrderStatusHistory create(Order order, OrderStatus fromStatus, OrderStatus toStatus, Long changedBy, String note) {
        return OrderStatusHistory.builder()
            .order(order)
            .fromStatus(fromStatus != null ? fromStatus.getValue() : null)
            .toStatus(toStatus.getValue())
            .changedBy(changedBy)
            .note(note)
            .build();
    }
    
    public static OrderStatusHistory create(Order order, OrderStatus toStatus, Long changedBy, String note) {
        return create(order, null, toStatus, changedBy, note);
    }
    
    // Helper methods (mantener la lógica de negocio)
    public OrderStatus getFromStatusEnum() {
        return fromStatus != null ? OrderStatus.fromValue(fromStatus) : null;
    }
    
    public OrderStatus getToStatusEnum() {
        return OrderStatus.fromValue(toStatus);
    }
}
