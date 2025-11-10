package com.ticket.app.services;

import com.ticket.app.domain.entities.Hold;
import java.util.List;
import java.util.UUID;

public interface HoldService {
    Hold createHold(UUID customerId, UUID occurrenceId, UUID eventVenueAreaId, UUID eventVenueSeatId, Integer quantity);
    void convertHold(UUID occurrenceId);
    void expireHolds();
    List<Hold> getAllActive();
}
