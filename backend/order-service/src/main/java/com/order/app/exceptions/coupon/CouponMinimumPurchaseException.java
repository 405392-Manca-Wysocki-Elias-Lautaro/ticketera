package com.order.app.exceptions.coupon;

/**
 * Exception thrown when the order doesn't meet the minimum purchase amount for a coupon.
 */
public class CouponMinimumPurchaseException extends CouponException {
    public CouponMinimumPurchaseException(String message) {
        super(message);
    }
}

