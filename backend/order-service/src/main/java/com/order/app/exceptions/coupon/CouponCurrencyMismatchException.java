package com.order.app.exceptions.coupon;

/**
 * Exception thrown when a coupon's currency doesn't match the order's currency.
 */
public class CouponCurrencyMismatchException extends CouponException {
    public CouponCurrencyMismatchException(String message) {
        super(message);
    }
}

