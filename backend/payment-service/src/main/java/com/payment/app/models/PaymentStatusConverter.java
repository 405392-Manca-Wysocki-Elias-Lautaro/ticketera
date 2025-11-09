package com.payment.app.models;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PaymentStatusConverter implements AttributeConverter<Payment.PaymentStatus, String> {
    
    @Override
    public String convertToDatabaseColumn(Payment.PaymentStatus status) {
        if (status == null) {
            return null;
        }
        return status.getValue();
    }
    
    @Override
    public Payment.PaymentStatus convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        
        for (Payment.PaymentStatus status : Payment.PaymentStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        
        throw new IllegalArgumentException("Unknown payment status: " + value);
    }
}

