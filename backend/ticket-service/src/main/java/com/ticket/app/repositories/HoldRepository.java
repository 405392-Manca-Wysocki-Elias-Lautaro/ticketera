package com.ticket.app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.ticket.app.domain.entities.Hold;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HoldRepository extends JpaRepository<Hold, UUID> {

    @Query("SELECT h FROM Hold h WHERE h.eventId = :eventId AND h.status = 'ACTIVE' AND h.expiresAt > CURRENT_TIMESTAMP")
    Optional<Hold> findActiveByeventId(UUID eventId);

    @Query("SELECT h FROM Hold h WHERE h.eventVenueSeatId = :seatId AND h.status = 'ACTIVE' AND h.expiresAt > CURRENT_TIMESTAMP")
    Optional<Hold> findActiveBySeatId(UUID seatId);

    @Query("SELECT h FROM Hold h WHERE h.status = 'ACTIVE' AND h.expiresAt < CURRENT_TIMESTAMP")
    List<Hold> findExpiredHolds();
}
