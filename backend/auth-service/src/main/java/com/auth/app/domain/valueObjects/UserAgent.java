package com.auth.app.domain.valueObjects;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public record UserAgent(String value) {
    public UserAgent {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User-Agent cannot be blank");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}

