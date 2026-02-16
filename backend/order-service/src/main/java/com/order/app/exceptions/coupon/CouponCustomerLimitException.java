package com.order.app.exceptions.coupon;

/**
 * Exception thrown when a customer has reached their maximum uses for a coupon.
 */
public class CouponCustomerLimitException extends CouponException {
    public CouponCustomerLimitException(String message) {
        super(message);
    }
}

