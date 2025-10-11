package com.event.app.exceptions;

public class VenueAreaNotFoundException extends RuntimeException {
    public VenueAreaNotFoundException(String message) {
        super(message);
    }

    public VenueAreaNotFoundException(Long id) {
        super("No se encontró el área de venue con ID: " + id);
    }
}

