package com.order.app.exceptions.coupon;

/**
 * Exception thrown when a coupon is not valid for the specific event.
 */
public class CouponEventRestrictionException extends CouponException {
    public CouponEventRestrictionException(String message) {
        super(message);
    }
}

