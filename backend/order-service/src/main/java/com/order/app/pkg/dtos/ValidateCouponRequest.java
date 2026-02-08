package com.order.app.pkg.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para validar un cupón en el checkout.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateCouponRequest {
    
    @NotBlank(message = "El código del cupón es requerido")
    private String code;
    
    @NotNull(message = "El ID del organizador es requerido")
    private UUID organizerId;
    
    @NotNull(message = "El ID del evento es requerido")
    private UUID eventId;
    
    @NotNull(message = "El subtotal es requerido")
    @Positive(message = "El subtotal debe ser positivo")
    private Long subtotalCents;
    
    @NotBlank(message = "La moneda es requerida")
    private String currency;
    
    private UUID customerId; // Opcional, para validar límite por cliente
}

