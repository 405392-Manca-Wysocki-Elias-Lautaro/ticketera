package com.event.app.exceptions;

import java.util.UUID;

public class OrganizerNotFoundException extends RuntimeException {
    public OrganizerNotFoundException(UUID id) {
        super("Organizer not found with id " + id);
    }
}
