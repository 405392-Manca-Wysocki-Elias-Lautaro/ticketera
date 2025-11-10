package com.order.app.models;

public enum OrderStatus {
    PENDING("pending"),
    PAID("paid"),
    FAILED("failed"),
    REFUNDED("refunded"),
    ;
    private final String value;
    
    OrderStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + value);
    }
    
    public boolean isPaid() {
        return this == PAID;
    }
    
    public boolean isFinal() {
        return this == PAID || this == REFUNDED;
    }
    
    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this) {
            case PENDING:
                return newStatus == PAID || newStatus == FAILED;
            case FAILED:
                return newStatus == PENDING;
            case PAID:
                return newStatus == REFUNDED;
            case REFUNDED:
                return false;
            default:
                return false;
        }
    }
}
