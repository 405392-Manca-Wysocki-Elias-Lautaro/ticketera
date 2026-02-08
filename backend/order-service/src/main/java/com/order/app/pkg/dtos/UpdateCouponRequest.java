package com.order.app.pkg.dtos;

import com.order.app.models.Coupon;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para actualizar un cupón existente.
 * Solo permite editar campos específicos según las reglas de negocio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCouponRequest {
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    private Coupon.CouponStatus status;
    
    @Min(value = 1, message = "El máximo de usos debe ser al menos 1")
    private Integer maxUses;
    
    @Min(value = 1, message = "El máximo de usos por cliente debe ser al menos 1")
    private Integer maxUsesPerCustomer;
    
    private LocalDateTime validFrom;
    
    private LocalDateTime validUntil;
    
    private List<UUID> eventIds;
    
    @PositiveOrZero(message = "El monto mínimo debe ser positivo o cero")
    private Long minPurchaseAmountCents;
    
    /**
     * Valida que las fechas sean coherentes si ambas están presentes.
     */
    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isValidDateRange() {
        if (validFrom == null || validUntil == null) {
            return true;
        }
        return validUntil.isAfter(validFrom);
    }
}

