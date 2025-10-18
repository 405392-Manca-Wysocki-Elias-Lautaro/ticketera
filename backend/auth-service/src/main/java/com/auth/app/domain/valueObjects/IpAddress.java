package com.auth.app.domain.valueObjects;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record IpAddress(String value) {
    public IpAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("IP address cannot be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
