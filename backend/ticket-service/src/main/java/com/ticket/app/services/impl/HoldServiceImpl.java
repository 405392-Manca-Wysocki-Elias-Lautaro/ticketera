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
    // 🎟️ Crear Hold (admisión general o asiento numerado)
    // ------------------------------------------------------------
    @Override
    @Transactional
    public Hold createHold(UUID customerId, UUID occurrenceId, UUID areaId, UUID seatId, Integer quantity) {
        if (seatId != null) {
            Optional<Hold> existing = holdRepository.findActiveBySeatId(seatId);
            if (existing.isPresent()) {
                throw new SeatAlreadyHeldException();
            }

            Hold hold = Hold.builder()
                    .customerId(customerId)
                    .occurrenceId(occurrenceId)
                    .eventVenueAreaId(areaId)
                    .eventVenueSeatId(seatId)
                    .quantity(1)
                    .status(HoldStatus.ACTIVE)
                    .expiresAt(OffsetDateTime.now().plusMinutes(5))
                    .build();

            return holdRepository.save(hold);
        }

        if (quantity == null || quantity <= 0) {
            throw new InvalidHoldQuantityException();
        }

        Hold hold = Hold.builder()
                .customerId(customerId)
                .occurrenceId(occurrenceId)
                .eventVenueAreaId(areaId)
                .quantity(quantity)
                .status(HoldStatus.ACTIVE)
                .expiresAt(OffsetDateTime.now().plusMinutes(5))
                .build();

        return holdRepository.save(hold);
    }

    // ------------------------------------------------------------
    // 🔁 Convertir Hold a CONVERTED (cuando se genera el ticket)
    // ------------------------------------------------------------
    @Override
    @Transactional
    public void convertHold(UUID occurrenceId) {
        Hold hold = holdRepository.findActiveByOccurrenceId(occurrenceId)
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
    // ⏱️ Expirar Holds vencidos
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
    // 🔍 Obtener todos los Holds activos
    // ------------------------------------------------------------
    @Override
    public List<Hold> getAllActive() {
        return holdRepository.findAll()
                .stream()
                .filter(h -> h.getStatus() == HoldStatus.ACTIVE)
                .toList();
    }
}
