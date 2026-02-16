package com.order.app.exceptions.coupon;

/**
 * Exception thrown when a coupon is not active.
 */
public class CouponInactiveException extends CouponException {
    public CouponInactiveException(String message) {
        super(message);
    }
}

