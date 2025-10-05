package com.event.app.exceptions;

public class OrganizerNotFoundException extends RuntimeException {
    public OrganizerNotFoundException(Long id) {
        super("Organizer not found with id " + id);
    }
}
