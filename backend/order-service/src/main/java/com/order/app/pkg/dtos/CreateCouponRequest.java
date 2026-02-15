package com.order.app.pkg.dtos;

import com.order.app.models.DiscountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para crear un nuevo cupón.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCouponRequest {
    
    @NotBlank(message = "El código del cupón es requerido")
    @Size(min = 3, max = 50, message = "El código debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "El código solo puede contener letras mayúsculas, números y guiones")
    private String code;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @NotNull(message = "El tipo de descuento es requerido")
    private DiscountType discountType;
    
    @NotNull(message = "El valor del descuento es requerido")
    @Positive(message = "El valor del descuento debe ser positivo")
    private Long discountValue;
    
    @Size(min = 3, max = 3, message = "La moneda debe ser un código de 3 letras (ej: ARS, USD)")
    @Builder.Default
    private String currency = "ARS";
    
    @Min(value = 1, message = "El máximo de usos debe ser al menos 1")
    private Integer maxUses;
    
    @Min(value = 1, message = "El máximo de usos por cliente debe ser al menos 1")
    private Integer maxUsesPerCustomer;
    
    @NotNull(message = "La fecha de inicio es requerida")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDateTime validFrom;
    
    @NotNull(message = "La fecha de fin es requerida")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDateTime validUntil;
    
    private List<UUID> eventIds;
    
    @PositiveOrZero(message = "El monto mínimo debe ser positivo o cero")
    private Long minPurchaseAmountCents;
    
    /**
     * Valida que las fechas sean coherentes.
     */
    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isValidDateRange() {
        if (validFrom == null || validUntil == null) {
            return true; // Dejamos que @NotNull maneje los nulls
        }
        return validUntil.isAfter(validFrom);
    }
    
    /**
     * Valida que el porcentaje esté en rango válido.
     */
    @AssertTrue(message = "Para descuento porcentual, el valor debe estar entre 1 y 100")
    public boolean isValidPercentage() {
        if (discountType == null || discountType != DiscountType.PERCENTAGE) {
            return true;
        }
        return discountValue != null && discountValue >= 1 && discountValue <= 100;
    }
    
    /**
     * Valida que para monto fijo se especifique la moneda.
     */
    @AssertTrue(message = "Para descuento de monto fijo, debe especificar la moneda")
    public boolean isValidCurrency() {
        if (discountType == null || discountType != DiscountType.FIXED_AMOUNT) {
            return true;
        }
        return currency != null && !currency.isBlank();
    }
}

