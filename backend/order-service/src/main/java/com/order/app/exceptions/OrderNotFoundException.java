package com.order.app.exceptions;

public class OrderNotFoundException extends RuntimeException {
    
    private final Long orderId;
    
    public OrderNotFoundException(Long orderId) {
        super("Order not found: " + orderId);
        this.orderId = orderId;
    }
    
    public OrderNotFoundException(Long orderId, String message) {
        super(message);
        this.orderId = orderId;
    }
    
    public Long getOrderId() {
        return orderId;
    }
}
