package com.ticket.app.services.impl;

import com.ticket.app.domain.entities.Hold;
import com.ticket.app.domain.enums.HoldStatus;
import com.ticket.app.exception.exceptions.HoldConversionException;
import com.ticket.app.exception.exceptions.HoldExpiredException;
import com.ticket.app.exception.exceptions.HoldNotFoundException;
import com.ticket.app.exception.exceptions.InvalidHoldQuantityException;
import com.ticket.app.exception.exceptions.SeatAlreadyHeldException;
import com.ticket.app.repositories.HoldRepository;
import com.ticket.app.services.HoldService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HoldServiceImpl implements HoldService {

    private final HoldRepository holdRepository;

    public HoldServiceImpl(HoldRepository holdRepository) {
        this.holdRepository = holdRepository;
    }

    // ------------------------------------------------------------
    // 🎟️ Create Hold (general admission or assigned seat)
    // ------------------------------------------------------------
    @Override
    @Transactional
    public Hold createHold(UUID customerId, UUID eventId, UUID areaId, UUID seatId, Integer quantity) {
        // 🎯 Seat-based hold
        if (seatId != null) {
            Optional<Hold> existing = holdRepository.findActiveBySeatId(seatId);
            if (existing.isPresent()) {
                throw new SeatAlreadyHeldException();
            }

            Hold hold = Hold.builder()
                    .customerId(customerId)
                    .eventId(eventId)
                    .eventVenueAreaId(areaId)
                    .eventVenueSeatId(seatId)
                    .quantity(1)
                    .status(HoldStatus.ACTIVE)
                    .expiresAt(OffsetDateTime.now().plusMinutes(5))
                    .build();

            return holdRepository.save(hold);
        }

        // 🎯 General Admission hold
        if (quantity == null || quantity <= 0) {
            throw new InvalidHoldQuantityException();
        }

        Hold hold = Hold.builder()
                .customerId(customerId)
                .eventId(eventId)
                .eventVenueAreaId(areaId)
                .quantity(quantity)
                .status(HoldStatus.ACTIVE)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .build();

        return holdRepository.save(hold);
    }

    // ------------------------------------------------------------
    // 🔁 Convert Hold to CONVERTED (when ticket is generated)
    // ------------------------------------------------------------
    @Override
    @Transactional
    public void convertHold(UUID eventId) {
        Hold hold = holdRepository.findActiveByeventId(eventId)
                .orElseThrow(HoldNotFoundException::new);

        if (hold.getStatus() != HoldStatus.ACTIVE) {
            throw new HoldConversionException();
        }

        if (hold.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new HoldExpiredException();
        }

        hold.setStatus(HoldStatus.CONVERTED);
        hold.setConvertedAt(OffsetDateTime.now());
        hold.setUpdatedAt(OffsetDateTime.now());
        holdRepository.save(hold);
    }

    // ------------------------------------------------------------
    // ⏱️ Expire all expired Holds
    // ------------------------------------------------------------
    @Override
    @Transactional
    public void expireHolds() {
        List<Hold> expired = holdRepository.findExpiredHolds();

        for (Hold hold : expired) {
            hold.setStatus(HoldStatus.EXPIRED);
            hold.setUpdatedAt(OffsetDateTime.now());
        }

        if (!expired.isEmpty()) {
            holdRepository.saveAll(expired);
        }
    }

    // ------------------------------------------------------------
    // 🔍 Retrieve all active Holds
    // ------------------------------------------------------------
    @Override
    public List<Hold> getAllActive() {
        return holdRepository.findAll()
                .stream()
                .filter(h -> h.getStatus() == HoldStatus.ACTIVE)
                .toList();
    }
}
