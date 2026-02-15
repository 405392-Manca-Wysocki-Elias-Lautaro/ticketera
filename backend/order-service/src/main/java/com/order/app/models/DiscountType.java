package com.order.app.models;

/**
 * Tipo de descuento que aplica el cupón.
 */
public enum DiscountType {
    /** Descuento porcentual (ej: 20% de descuento) */
    PERCENTAGE,
    /** Monto fijo en centavos (ej: $5000 de descuento) */
    FIXED_AMOUNT
}
