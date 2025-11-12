package com.event.app.exceptions;

import java.util.UUID;

public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException(UUID id) {
        super("Seat not found with ID: " + id);
    }
}

