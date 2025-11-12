package com.event.app.exceptions;

import java.util.UUID;

public class AreaNotFoundException extends RuntimeException {
    public AreaNotFoundException(UUID id) {
        super("Area not found with ID: " + id);
    }
}

