package com.order.app.pkg.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de validación de cupón.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateCouponResponse {
    
    private boolean valid;
    private CouponDTO coupon;
    private Long discountCents;
    private String errorMessage;
    
    /**
     * Respuesta exitosa con cupón válido.
     */
    public static ValidateCouponResponse success(CouponDTO coupon, Long discountCents) {
        return ValidateCouponResponse.builder()
            .valid(true)
            .coupon(coupon)
            .discountCents(discountCents)
            .build();
    }
    
    /**
     * Respuesta de error con mensaje.
     */
    public static ValidateCouponResponse error(String errorMessage) {
        return ValidateCouponResponse.builder()
            .valid(false)
            .errorMessage(errorMessage)
            .build();
    }
}

