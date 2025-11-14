package com.ticket.app.services;

import com.ticket.app.domain.entities.Hold;
import java.util.List;
import java.util.UUID;

public interface HoldService {
    Hold createHold(UUID customerId, UUID eventId, UUID eventVenueAreaId, UUID eventVenueSeatId, Integer quantity);
    void convertHold(UUID eventId);
    void expireHolds();
    List<Hold> getAllActive();
}
