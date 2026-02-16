package com.order.app.exceptions.coupon;

/**
 * Exception thrown when a coupon has reached its maximum uses.
 */
public class CouponExhaustedException extends CouponException {
    public CouponExhaustedException(String message) {
        super(message);
    }
}

