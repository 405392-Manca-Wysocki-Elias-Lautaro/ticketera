package com.order.app.exceptions.coupon;

/**
 * Exception thrown when a coupon is not found.
 */
public class CouponNotFoundException extends CouponException {
    public CouponNotFoundException(String message) {
        super(message);
    }
}

