package com.order.app.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa el uso/redención de un cupón en una orden.
 * Mantiene el historial de todos los cupones aplicados para reportes y auditoría.
 */
@Entity
@Table(name = "coupon_redemptions", schema = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CouponRedemption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;
    
    @Column(name = "coupon_id", nullable = false)
    private UUID couponId;
    
    @Column(name = "order_id", nullable = false)
    private UUID orderId;
    
    @Column(name = "customer_id", nullable = false)
    private UUID customerId;
    
    /**
     * Monto del descuento aplicado en centavos.
     */
    @Column(name = "discount_applied_cents", nullable = false)
    private Long discountAppliedCents;
    
    /**
     * Subtotal de la orden antes del descuento en centavos.
     */
    @Column(name = "subtotal_cents", nullable = false)
    private Long subtotalCents;
    
    @CreationTimestamp
    @Column(name = "redeemed_at", nullable = false, updatable = false)
    private LocalDateTime redeemedAt;
    
    // ========================================
    // MÉTODOS DE UTILIDAD
    // ========================================
    
    /**
     * Calcula el total final después del descuento.
     */
    public Long getFinalAmountCents() {
        return subtotalCents - discountAppliedCents;
    }
    
    /**
     * Calcula el porcentaje de descuento aplicado.
     */
    public Double getDiscountPercentage() {
        if (subtotalCents == 0) {
            return 0.0;
        }
        return (discountAppliedCents * 100.0) / subtotalCents;
    }
}

