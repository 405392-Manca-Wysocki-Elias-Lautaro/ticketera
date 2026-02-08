package com.order.app.pkg.dtos;

import com.order.app.models.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para representar un cupón en las respuestas de la API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDTO {
    
    private UUID id;
    private UUID organizerId;
    private String code;
    private String description;
    private Coupon.DiscountType discountType;
    private Long discountValue;
    private String currency;
    private Integer maxUses;
    private Integer maxUsesPerCustomer;
    private Integer currentUses;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private List<UUID> eventIds;
    private Long minPurchaseAmountCents;
    private Coupon.CouponStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    
    /**
     * Convierte una entidad Coupon a DTO.
     */
    public static CouponDTO fromEntity(Coupon coupon) {
        return CouponDTO.builder()
            .id(coupon.getId())
            .organizerId(coupon.getOrganizerId())
            .code(coupon.getCode())
            .description(coupon.getDescription())
            .discountType(coupon.getDiscountType())
            .discountValue(coupon.getDiscountValue())
            .currency(coupon.getCurrency())
            .maxUses(coupon.getMaxUses())
            .maxUsesPerCustomer(coupon.getMaxUsesPerCustomer())
            .currentUses(coupon.getCurrentUses())
            .validFrom(coupon.getValidFrom())
            .validUntil(coupon.getValidUntil())
            .eventIds(coupon.getEventIds())
            .minPurchaseAmountCents(coupon.getMinPurchaseAmountCents())
            .status(coupon.getStatus())
            .createdAt(coupon.getCreatedAt())
            .updatedAt(coupon.getUpdatedAt())
            .createdBy(coupon.getCreatedBy())
            .build();
    }
    
    /**
     * Calcula si el cupón está disponible actualmente.
     */
    public boolean isCurrentlyAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return status == Coupon.CouponStatus.ACTIVE
            && !now.isBefore(validFrom)
            && !now.isAfter(validUntil)
            && (maxUses == null || currentUses < maxUses);
    }
    
    /**
     * Calcula el porcentaje de uso.
     */
    public Double getUsagePercentage() {
        if (maxUses == null || maxUses == 0) {
            return null;
        }
        return (currentUses * 100.0) / maxUses;
    }
}

