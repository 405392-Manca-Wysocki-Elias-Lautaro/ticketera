package com.event.app.services;

import com.event.app.dtos.SeatDTO;
import com.event.app.models.Seat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISeatService {
    Seat createSeat(SeatDTO seatDTO);
    Optional<Seat> getSeatById(UUID id);
    List<Seat> getSeatsByAreaId(UUID areaId);
    List<Seat> getAllSeats();
    Seat updateSeat(UUID id, SeatDTO seatDTO);
    void deleteSeat(UUID id);
}

