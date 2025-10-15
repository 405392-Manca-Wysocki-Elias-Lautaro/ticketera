package com.event.app.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.event.app.dtos.GenerateSeatsRequestDTO;
import com.event.app.dtos.VenueAreaDTO;
import com.event.app.models.VenueArea;
import com.event.app.models.VenueSeat;

public interface IVenueAreaService {

    VenueArea createVenueArea(VenueAreaDTO venueAreaDTO);

    Optional<VenueArea> getVenueAreaById(UUID id);

    List<VenueArea> getAllVenueAreas();

    List<VenueArea> getVenueAreasByVenueId(UUID venueId);

    VenueArea updateVenueArea(UUID id, VenueAreaDTO venueAreaDTO);

    void deleteVenueArea(UUID id);

    // Endpoints especiales para Seats
    List<VenueSeat> getSeatsByVenueAreaId(UUID venueAreaId);

    List<VenueSeat> generateSeats(UUID venueAreaId, GenerateSeatsRequestDTO request);
}

