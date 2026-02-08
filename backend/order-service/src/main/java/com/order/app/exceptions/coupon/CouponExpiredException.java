package com.order.app.exceptions.coupon;

/**
 * Exception thrown when a coupon has expired.
 */
public class CouponExpiredException extends CouponException {
    public CouponExpiredException(String message) {
        super(message);
    }
}

