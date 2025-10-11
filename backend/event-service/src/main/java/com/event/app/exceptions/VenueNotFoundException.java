package com.event.app.exceptions;

import java.util.UUID;

public class VenueNotFoundException extends RuntimeException {
    public VenueNotFoundException(UUID id) {
        super("Venue not found with id " + id);
    }
}

