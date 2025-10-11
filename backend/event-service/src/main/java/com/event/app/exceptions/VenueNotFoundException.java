package com.event.app.exceptions;

public class VenueNotFoundException extends RuntimeException {
    public VenueNotFoundException(Long id) {
        super("Venue not found with id " + id);
    }
}

