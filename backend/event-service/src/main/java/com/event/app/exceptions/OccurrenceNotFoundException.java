package com.event.app.exceptions;

import java.util.UUID;

public class OccurrenceNotFoundException extends RuntimeException {
    public OccurrenceNotFoundException(UUID id) {
        super("Event Occurrence not found with id " + id);
    }
}

