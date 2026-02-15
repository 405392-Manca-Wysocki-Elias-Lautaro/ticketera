package com.order.app.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa un cupón de descuento creado por un organizador.
 * Permite aplicar descuentos porcentuales o de monto fijo en órdenes.
 */
@Entity
@Table(name = "coupons", schema = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"eventIds"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;
    
    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;
    
    @Column(nullable = false, length = 50)
    private String code;
    
    @Column(length = 500)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;
    
    @Column(name = "discount_value", nullable = false)
    private Long discountValue;
    
    @Column(nullable = false)
    @Builder.Default
    private String currency = "ARS";
    
    @Column(name = "max_uses")
    private Integer maxUses;
    
    @Column(name = "max_uses_per_customer")
    private Integer maxUsesPerCustomer;
    
    @Column(name = "current_uses", nullable = false)
    @Builder.Default
    private Integer currentUses = 0;
    
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;
    
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;
    
    @Column(name = "event_ids", columnDefinition = "uuid[]")
    private List<UUID> eventIds;
    
    @Column(name = "min_purchase_amount_cents")
    private Long minPurchaseAmountCents;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CouponStatus status = CouponStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "created_by")
    private UUID createdBy;
    
    // ========================================
    // MÉTODOS DE NEGOCIO
    // ========================================
    
    /**
     * Verifica si el cupón está activo y puede ser usado.
     */
    public boolean isActive() {
        return status == CouponStatus.ACTIVE && deletedAt == null;
    }
    
    /**
     * Verifica si el cupón ha expirado por fecha.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(validUntil);
    }
    
    /**
     * Verifica si el cupón alcanzó su límite de usos.
     */
    public boolean isExhausted() {
        return maxUses != null && currentUses >= maxUses;
    }
    
    /**
     * Verifica si el cupón es válido para una fecha específica.
     */
    public boolean isValidForDate(LocalDateTime date) {
        return !date.isBefore(validFrom) && !date.isAfter(validUntil);
    }
    
    /**
     * Verifica si el cupón es válido para un evento específico.
     * Si eventIds está vacío o es null, el cupón aplica a todos los eventos.
     */
    public boolean isValidForEvent(UUID eventId) {
        return eventIds == null || eventIds.isEmpty() || eventIds.contains(eventId);
    }
    
    /**
     * Verifica si el monto de compra cumple con el mínimo requerido.
     */
    public boolean meetsMinimumPurchase(Long amountCents) {
        return minPurchaseAmountCents == null || amountCents >= minPurchaseAmountCents;
    }
    
    /**
     * Calcula el monto de descuento a aplicar según el tipo de cupón.
     * 
     * @param subtotalCents Subtotal de la orden en centavos
     * @return Monto del descuento en centavos (nunca mayor al subtotal)
     */
    public long calculateDiscount(long subtotalCents) {
        if (discountType == DiscountType.PERCENTAGE) {
            // Para porcentaje: (subtotal * porcentaje) / 100
            return (subtotalCents * discountValue) / 100;
        } else {
            // Para monto fijo: el menor entre el valor del cupón y el subtotal
            return Math.min(discountValue, subtotalCents);
        }
    }
    
    /**
     * Marca el cupón como soft-deleted.
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = CouponStatus.INACTIVE;
    }
    
    /**
     * Incrementa el contador de usos del cupón.
     */
    public void incrementUses() {
        this.currentUses++;
    }
}

