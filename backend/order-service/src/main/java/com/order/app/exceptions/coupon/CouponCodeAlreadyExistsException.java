package com.order.app.exceptions.coupon;

/**
 * Exception thrown when a coupon code already exists for an organizer.
 */
public class CouponCodeAlreadyExistsException extends CouponException {
    public CouponCodeAlreadyExistsException(String message) {
        super(message);
    }
}

