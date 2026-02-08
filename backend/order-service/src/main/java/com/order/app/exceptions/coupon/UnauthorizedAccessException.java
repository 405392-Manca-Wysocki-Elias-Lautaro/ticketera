package com.order.app.exceptions.coupon;

/**
 * Exception thrown when user doesn't have permission to access a coupon.
 */
public class UnauthorizedAccessException extends CouponException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

