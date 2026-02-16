package com.order.app.models;

/**
 * Estado del cupón.
 */
public enum CouponStatus {
    /** Cupón activo y usable */
    ACTIVE,
    /** Cupón desactivado manualmente */
    INACTIVE,
    /** Cupón expirado por fecha */
    EXPIRED,
    /** Cupón que alcanzó su límite de usos */
    EXHAUSTED
}
