package com.order.app.exceptions.coupon;

/**
 * Exception thrown when a coupon is not yet valid.
 */
public class CouponNotYetValidException extends CouponException {
    public CouponNotYetValidException(String message) {
        super(message);
    }
}

