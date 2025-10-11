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

    Optional<VenueArea> getVenueAreaById(Long id);

    List<VenueArea> getAllVenueAreas(); //VER ESTE CASO DE USO

    List<VenueArea> getVenueAreasByVenueId(UUID venueId);

    VenueArea updateVenueArea(Long id, VenueAreaDTO venueAreaDTO);

    void deleteVenueArea(Long id);

    // Endpoints especiales para Seats
    List<VenueSeat> getSeatsByVenueAreaId(Long venueAreaId);

    List<VenueSeat> generateSeats(Long venueAreaId, GenerateSeatsRequestDTO request);
}

