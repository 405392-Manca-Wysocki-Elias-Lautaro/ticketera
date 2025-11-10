package com.event.app.exceptions;

import java.util.UUID;

public class VenueAreaNotFoundException extends RuntimeException {
    public VenueAreaNotFoundException(String message) {
        super(message);
    }

    public VenueAreaNotFoundException(UUID id) {
        super("No se encontró el área de venue con ID: " + id);
    }
}

